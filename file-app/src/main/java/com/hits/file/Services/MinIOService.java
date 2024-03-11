package com.hits.file.Services;

import com.hits.common.Models.User.UserDto;
import com.hits.file.Mappers.FileMapper;
import com.hits.file.Models.Dto.FileDto.FileDto;
import com.hits.file.Models.Dto.Response.Response;
import com.hits.file.Models.Entities.File;
import com.hits.file.Repositories.FileRepository;
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
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinIOService implements IMinIOService{
    private final S3Client s3Client;
    private final FileRepository fileRepository;

    public ResponseEntity<?> uploadFile(UserDto user, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        File newFile;

        try {
            String login = user.getLogin().toLowerCase(); //Будет некорректно работать, если юзеры имеют одинаковые логины
                                                          //с отличием в заглавных буквах
            if (s3Client.listBuckets().buckets().stream().noneMatch(bucket -> bucket.name().equals(login))){
                s3Client.createBucket(CreateBucketRequest.builder().bucket(login).build());
            }

            if(s3Client.listObjects(ListObjectsRequest.builder().bucket(login).build()).contents().stream()
                    .anyMatch(object -> object.key().equals(filename))){
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Вы уже загрузили файл с таким же названием"), HttpStatus.BAD_REQUEST);
            }

            PutObjectRequest obj = PutObjectRequest.builder()
                    .bucket(login)
                    .key(filename)
                    .build();
            s3Client.putObject(obj, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            newFile = FileMapper.multipartFileToFile(file, user);
            fileRepository.save(newFile);
        }
        catch (S3Exception e) {
            throw new IOException("Ошибка при загрузке файла в MinIO");
        }

        return ResponseEntity.ok(newFile.getId());
    }

    public ResponseEntity<?> downloadFile(UserDto user, UUID id){
        File file = fileRepository.findFileByIdAndUserId(id, user.getId());

        if (file == null){
            return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                    "Файл с данным id не найден"), HttpStatus.NOT_FOUND);
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

    public ResponseEntity<?> getAllFiles(UserDto user){
        List<FileDto> files = fileRepository.findAllByUserId(user.getId())
                .stream()
                .map(FileMapper::fileToFileDto)
                .toList();

        return ResponseEntity.ok(files);
    }
}
