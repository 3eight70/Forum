package com.hits.FileSystem.Controllers;

import com.hits.FileSystem.Models.Dto.Response.Response;
import com.hits.FileSystem.Services.MinIOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MinIOController {
    private final MinIOService minIOService;

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file){
        try{
            return minIOService.uploadFile(file);
        }
        catch (IOException e) {
            e.printStackTrace();

            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Ошибка при загрузке файла в MinIO"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/file/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable("filename") UUID fileId){
        try{
            return minIOService.downloadFile(fileId);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Ошибка при скачивании файла из MinIO"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
