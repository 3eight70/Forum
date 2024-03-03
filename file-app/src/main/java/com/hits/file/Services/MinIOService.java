package com.hits.file.Services;

import com.hits.common.Utils.JwtUtils;
import com.hits.file.Mappers.FileMapper;
import com.hits.file.Models.Dto.FileDto.FileDto;
import com.hits.file.Models.Dto.Response.Response;
<<<<<<< HEAD
import com.hits.file.Models.Entity.File;
import com.hits.file.Repositories.FileRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
=======
import com.hits.file.Models.Entities.File;
import com.hits.file.Repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

<<<<<<< HEAD
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
=======
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinIOService implements IMinIOService{
    private final S3Client s3Client;
    private final FileRepository fileRepository;
<<<<<<< HEAD
    private final MinioClient minioClient;
=======
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d

    @Value("${jwt.secret}")
    private String secret;

    public ResponseEntity<?> uploadFile(String token, MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        File newFile;
<<<<<<< HEAD
        Claims claims = JwtUtils.parseToken(token.replace("Bearer ", ""), secret);

        try {
            String email = claims.getSubject().replace("@", "");
=======

        try {
            String email = JwtUtils.getUserEmail(token, secret).replace("@", "");
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d

            if (s3Client.listBuckets().buckets().stream().noneMatch(bucket -> bucket.name().equals(email))){
                s3Client.createBucket(CreateBucketRequest.builder().bucket(email).build());
            }

            if(s3Client.listObjects(ListObjectsRequest.builder().bucket(email).build()).contents().stream()
                    .anyMatch(object -> object.key().equals(filename))){
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Вы уже загрузили файл с таким же названием"), HttpStatus.BAD_REQUEST);
            }

            PutObjectRequest obj = PutObjectRequest.builder()
                    .bucket(email)
                    .key(filename)
                    .build();
            s3Client.putObject(obj, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

<<<<<<< HEAD
            newFile = FileMapper.multipartFileToFile(file, UUID.fromString((String) claims.get("userId")));
=======
            newFile = FileMapper.multipartFileToFile(file, JwtUtils.getUserIdFromToken(token, secret));
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
            fileRepository.save(newFile);
        }
        catch (S3Exception e) {
            throw new IOException("Ошибка при загрузке файла в MinIO");
        }

        return ResponseEntity.ok(newFile.getId());
    }

<<<<<<< HEAD
    public ResponseEntity<?> downloadFile(String token, UUID id) throws Exception{
        Claims claims = JwtUtils.parseToken(token.replace("Bearer ", ""), secret);
        File file = fileRepository.findFileByIdAndUser(id, UUID.fromString((String) claims.get("userId")));
=======
    public ResponseEntity<?> downloadFile(String token, UUID id){
        File file = fileRepository.findFileByIdAndUserId(id, JwtUtils.getUserIdFromToken(token, secret));
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d

        if (file == null){
            return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                    "Файл с данным id не найден"), HttpStatus.NOT_FOUND);
        }

<<<<<<< HEAD
        try {

            String presignedUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(claims.getSubject().replace("@", ""))
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

    public ResponseEntity<?> getAllFiles(String token){
        Claims claims = JwtUtils.parseToken(token.replace("Bearer ", ""), secret);
        List<FileDto> files = fileRepository.findAllByUser(UUID.fromString((String) claims.get("userId")))
=======
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

    public ResponseEntity<?> getAllFiles(String token){
        List<FileDto> files = fileRepository.findAllByUserId(JwtUtils.getUserIdFromToken(token, secret))
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
                .stream()
                .map(FileMapper::fileToFileDto)
                .toList();

        return ResponseEntity.ok(files);
    }
}
