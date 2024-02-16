package com.hits.FileSystem.Controllers;

import com.hits.FileSystem.Models.Dto.Response.Response;
import com.hits.FileSystem.Models.Entity.User;
import com.hits.FileSystem.Services.IMinIOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MinIOController {
    private final IMinIOService minIOService;

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file){
        try{
            return minIOService.uploadFile(user, file);
        }
        catch (IOException e) {
            e.printStackTrace();

            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Ошибка при загрузке файла в MinIO"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/file/download/{filename}")
    public ResponseEntity<?> downloadFile(@AuthenticationPrincipal User user, @PathVariable("filename") UUID fileId){
        try{
            return minIOService.downloadFile(user, fileId);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Ошибка при скачивании файла из MinIO"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/file/get")
    public ResponseEntity<?> getFiles(@AuthenticationPrincipal User user){
        try{
            return minIOService.getAllFiles(user);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
