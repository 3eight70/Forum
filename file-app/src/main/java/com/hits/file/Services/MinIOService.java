package com.hits.file.Services;

import com.hits.file.Mappers.FileMapper;
import com.hits.file.Models.Dto.FileDto.FileDto;
import com.hits.file.Models.Dto.Response.Response;
import com.hits.file.Models.Entity.File;
import com.hits.file.Models.Entity.User;
import com.hits.file.Repositories.FileRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinIOService implements IMinIOService{
    private final S3Client s3Client;
    private final FileRepository fileRepository;
    private final MinioClient minioClient;

    public ResponseEntity<?> uploadFile(User user, MultipartFile file) throws IOException{
        String filename = file.getOriginalFilename();
        File newFile;

        try {
            if (s3Client.listBuckets().buckets().stream().noneMatch(bucket -> bucket.name().equals(user.getName()))){
                s3Client.createBucket(CreateBucketRequest.builder().bucket(user.getName()).build());
            }

            if(s3Client.listObjects(ListObjectsRequest.builder().bucket(user.getName()).build()).contents().stream()
                    .anyMatch(object -> object.key().equals(filename))){
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Вы уже загрузили файл с таким же названием"), HttpStatus.BAD_REQUEST);
            }

            PutObjectRequest obj = PutObjectRequest.builder()
                    .bucket(user.getName())
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

    public ResponseEntity<?> downloadFile(User user, UUID id) throws Exception{
        File file = fileRepository.findFileByIdAndUser(id, user);

        if (file == null){
            return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                    "Файл с данным id не найден"), HttpStatus.NOT_FOUND);
        }

        try {

            String presignedUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(user.getName())
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

    public ResponseEntity<?> getAllFiles(User user){
        List<FileDto> files = fileRepository.findAllByUser(user)
                .stream()
                .map(FileMapper::fileToFileDto)
                .toList();

        return ResponseEntity.ok(files);
    }
}
