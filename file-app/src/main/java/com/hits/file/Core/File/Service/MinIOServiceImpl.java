package com.hits.file.Core.File.Service;

import com.hits.common.Core.File.DTO.FileDto;
import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.*;
import com.hits.file.Core.File.Entity.File;
import com.hits.file.Core.File.Mapper.FileMapper;
import com.hits.file.Core.File.Repository.FileRepository;
import com.hits.security.Rest.Client.ForumAppClient;
import feign.FeignException;
import io.minio.*;
import io.minio.errors.MinioException;
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
public class MinIOServiceImpl implements MinIOService {
    private final MinioClient minioClient;
    private final FileRepository fileRepository;
    private final ForumAppClient forumAppClient;

    @Transactional
    public UUID uploadFile(UserDto user,MultipartFile file)
            throws IOException, BadRequestException{
        String filename = file.getOriginalFilename();
        String author = user.getLogin();
        File newFile;

        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(author).build())){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(author).build());
            }

            File checkFile = fileRepository.findFileByAuthorLoginAndNameContainsIgnoreCase(author, filename);

            if (checkFile != null){
                throw new BadRequestException("Вы уже загрузили файл с таким же названием");
            }

            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(user.getLogin())
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            newFile = FileMapper.multipartFileToFile(file, author);
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
        try {
            File file = fileRepository.findFileById(id)
                    .orElseThrow(() -> new NotFoundException(String.format("Файл с id=%s не найдет", id)));

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(file.getAuthorLogin())
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
        catch (FeignException.NotFound e){
            throw new NotFoundException("Сообщение не найдено");
        }
        catch (Exception e){
            throw new UnknownException();
        }
    }

    public ResponseEntity<?> getAllFiles(String author){
        List<FileDto> files = fileRepository.findAllByAuthorLogin(author)
                .stream()
                .map(FileMapper::fileToFileDto)
                .toList();

        return ResponseEntity.ok(files);
    }

    @Transactional
    public ResponseEntity<?> deleteFile(UserDto user, UUID messageId ,UUID fileId) throws IOException{
        File file = fileRepository.findFileById(fileId)
                .orElseThrow(() -> new NotFoundException(String.format("Файл с id=%s не найдет", fileId)));


        try {
            MessageDto messageDto = forumAppClient.checkMessage(messageId).getBody();

            if (messageDto == null){
                throw new NotFoundException(String.format("Сообщение с id=%s не найдено", messageId));
            }

            if (user.getRole() != Role.ADMIN) {
                if (!Objects.equals(user.getLogin(), messageDto.getAuthorLogin()) &&
                        user.getRole() != Role.MODERATOR && user.getManageCategoryId().contains(messageDto.getCategoryId())) {
                    throw new ForbiddenException();
                }
            }

            minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(messageDto.getAuthorLogin())
                            .object(file.getName())
                            .build());

            fileRepository.delete(file);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Вложение успешно удалено"), HttpStatus.OK);
        }
        catch (IOException e){
            throw new IOException("Ошибка при удалении файла");
        }
        catch (FeignException.NotFound e) {
            throw new NotFoundException("Сообщение не найдено");
        }
        catch (Exception e){
            throw new UnknownException();
        }
    }

    public ResponseEntity<FileDto> checkFile(UUID fileId) throws NotFoundException{
        File file = fileRepository.findFileById(fileId)
                .orElseThrow(() -> new NotFoundException("Файл не найден"));

        return ResponseEntity.ok(FileMapper.fileToFileDto(file));
    }
}
