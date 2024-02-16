package com.hits.FileSystem.Services;

import com.hits.FileSystem.Mappers.FileMapper;
import com.hits.FileSystem.Models.Dto.Response.Response;
import com.hits.FileSystem.Models.Entity.File;
import com.hits.FileSystem.Repositories.FileRepository;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinIOService {
    private final S3Client s3Client;
    private final FileRepository fileRepository;
    private final MinioClient minioClient;

    private String bucketName = "final";  //изменить на почту пользователя

    public ResponseEntity<?> uploadFile(MultipartFile file) throws IOException{
        String filename = file.getOriginalFilename();
        File newFile;

        try {
            if (s3Client.listBuckets().buckets().stream().noneMatch(bucket -> bucket.name().equals(bucketName))){
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            }

            if(s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build()).contents().stream()
                    .anyMatch(object -> object.key().equals(filename))){
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Вы уже загрузили файл с таким же названием"), HttpStatus.BAD_REQUEST);
            }

            PutObjectRequest obj = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();
            s3Client.putObject(obj, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            newFile = FileMapper.multipartFileToFile(file);
            fileRepository.save(newFile);
        }
        catch (S3Exception e) {
            throw new IOException("Ошибка при загрузке файла в MinIO");
        }

        return ResponseEntity.ok(newFile.getId());
    }

    public ResponseEntity<?> downloadFile(UUID id) throws Exception{
        File file = fileRepository.findFileById(id);

        if (file == null){
            return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                    "Файл с данным id не найден"), HttpStatus.NOT_FOUND);
        }


        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(file.getName())
                            .expiry(1, TimeUnit.DAYS)
                    .build());

            return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                    presignedUrl), HttpStatus.OK);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                "Вы еще не загрузили ни одного файла"), HttpStatus.NOT_FOUND);
    }
}
