package com.hits.file.Services;

import com.hits.common.Core.Message.DTO.MessageDto;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.*;
import com.hits.file.Mappers.FileMapper;
import com.hits.file.Models.Dto.FileDto.FileDto;
import com.hits.file.Models.Entities.File;
import com.hits.file.Repositories.FileRepository;
import com.hits.security.Rest.Client.ForumAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinIOService implements IMinIOService{
    private final S3Client s3Client;
    private final FileRepository fileRepository;
    private final ForumAppClient forumAppClient;

    public UUID uploadFile(UUID messageId, MultipartFile file)
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
            if (s3Client.listBuckets().buckets()
                    .stream()
                    .noneMatch(bucket -> bucket.name().equals(messageId.toString()))){
                s3Client.createBucket(CreateBucketRequest.builder().bucket(messageId.toString()).build());
            }

            List<S3Object> objects = s3Client.listObjects(ListObjectsRequest.builder().bucket(messageId.toString()).build()).contents();
            if (objects.stream()
                    .anyMatch(object -> object.key().equals(filename))){
                throw new BadRequestException("Вы уже загрузили файл с таким же названием");
            }
            else if (objects.size() == 5){
                throw new FileLimitException();
            }

            PutObjectRequest obj = PutObjectRequest.builder()
                    .bucket(messageId.toString())
                    .key(filename)
                    .build();
            s3Client.putObject(obj, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            newFile = FileMapper.multipartFileToFile(file, messageId);
            fileRepository.save(newFile);
        }
        catch (S3Exception e) {
            throw new IOException("Ошибка при загрузке файла в MinIO");
        }

        return newFile.getId();
    }

    public ResponseEntity<?> downloadFile(UUID id)
    throws NotFoundException {
        File file = fileRepository.findFileById(id);

        if (file == null){
            throw new NotFoundException(String.format("Файл с id=%s не найден", id));
        }

        byte[] fileBytes = file.getFileContent();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + file.getName());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    public ResponseEntity<?> getAllFiles(UUID messageId){
        List<FileDto> files = fileRepository.findAllByMessageId(messageId)
                .stream()
                .map(FileMapper::fileToFileDto)
                .toList();

        return ResponseEntity.ok(files);
    }

    public ResponseEntity<?> deleteFile(UserDto user,UUID messageId ,UUID fileId){
        MessageDto messageDto = forumAppClient.checkMessage(messageId).getBody();

        if (messageDto != null) {
            if (user.getRole() != Role.ADMIN) {
                if  (!Objects.equals(user.getLogin(), messageDto.getAuthorLogin()) ||
                        user.getRole() != Role.MODERATOR || user.getManageCategoryId() != messageDto.getCategoryId()) {
                    throw new ForbiddenException();
                }
            }

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(messageId.toString())
                    .key(fileId.toString())
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    "Вложение успешно удалено"), HttpStatus.OK);
        }
        else if (messageDto == null){
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        throw new UnknownException();
    }
}
