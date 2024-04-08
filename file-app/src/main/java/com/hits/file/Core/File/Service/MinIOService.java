package com.hits.file.Core.File.Service;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.*;
import com.hits.file.Core.File.Mapper.FileMapper;
import com.hits.file.Core.File.Models.FileDto;
import com.hits.file.Core.File.Entity.File;
import com.hits.file.Core.File.Repository.FileRepository;
import com.hits.security.Rest.Client.ForumAppClient;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinIOService implements IMinIOService{
    private final MinioClient minioClient;
    private final FileRepository fileRepository;
    private final ForumAppClient forumAppClient;

    @Transactional
    public UUID uploadFile(UUID messageId, MultipartFile file, UUID fileId)
            throws IOException, BadRequestException{
        String filename = file.getOriginalFilename();
        File newFile;


//        try {
//            ResponseEntity<MessageDto> forumMessage = forumAppClient.checkMessage(messageId);
//            System.out.println(forumMessage);
//        }
//        catch (FeignException.NotFound e) {
//            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
//        }  Не получается перекрестная проверка, т.к сообщение еще не сохранено при загрузке файла

        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(messageId.toString()).build())){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(messageId.toString()).build());
            }

            File checkFile = fileRepository.findFileByMessageIdAndNameContainsIgnoreCase(messageId, filename);

            if (checkFile != null){
                throw new BadRequestException("Вы уже загрузили файл с таким же названием");
            }

            ListObjectsArgs objects = ListObjectsArgs.builder()
                    .bucket(messageId.toString())
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(objects);
            int count = 0;

            for (Result<Item> result : results){
                count++;
            }

            if (count == 5){
                throw new FileLimitException();
            }

            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(messageId.toString())
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            newFile = FileMapper.multipartFileToFile(file, messageId, fileId);
            fileRepository.save(newFile);
        }
        catch (MinioException e) {
            throw new IOException("Ошибка при загрузке файла в MinIO");
        }
        catch (BadRequestException | FileLimitException e) {
            throw e;
        }
        catch (Exception e){
            throw new UnknownException();
        }

        return newFile.getId();
    }

    public ResponseEntity<?> downloadFile(UUID id)
    throws NotFoundException, IOException{
        File file = fileRepository.findFileById(id);

        if (file == null){
            throw new NotFoundException(String.format("Файл с id=%s не найден", id));
        }

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(file.getMessageId().toString())
                            .object(file.getName())
                            .build());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + file.getName());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(stream));
        }
        catch (MinioException e){
            throw new IOException("Ошибка при скачивании файла");
        }
        catch (Exception e){
            throw new UnknownException();
        }
    }

    public ResponseEntity<?> getAllFiles(UUID messageId){
        List<FileDto> files = fileRepository.findAllByMessageId(messageId)
                .stream()
                .map(FileMapper::fileToFileDto)
                .toList();

        return ResponseEntity.ok(files);
    }

    @Transactional
    public ResponseEntity<?> deleteFile(UserDto user,UUID messageId ,UUID fileId) throws IOException{
        MessageDto messageDto = forumAppClient.checkMessage(messageId).getBody();
        File file = fileRepository.findFileById(fileId);

        if (file == null){
            throw new NotFoundException(String.format("Файл с id=%s не найдет", fileId));
        }

        if (messageDto != null) {
            if (user.getRole() != Role.ADMIN) {
                if  (!Objects.equals(user.getLogin(), messageDto.getAuthorLogin()) &&
                        user.getRole() != Role.MODERATOR && user.getManageCategoryId() != messageDto.getCategoryId()) {
                    throw new ForbiddenException();
                }
            }

            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                                .bucket(messageId.toString())
                                .object(file.getName())
                                .build());

                fileRepository.delete(file);

                return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                        "Вложение успешно удалено"), HttpStatus.OK);
            }
            catch (IOException e){
                throw new IOException("Ошибка при удалении файла");
            }
            catch (Exception e){
                throw new UnknownException();
            }
        }
        else {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }
    }
}
