package com.hits.file.Controllers;

<<<<<<< HEAD
=======

>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import com.hits.file.Models.Dto.Response.Response;
import com.hits.file.Services.IMinIOService;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import org.springframework.http.HttpHeaders;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

<<<<<<< HEAD
=======
import static com.hits.common.Consts.*;

>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
@RestController
@RequiredArgsConstructor
public class MinIOController {
    private final IMinIOService minIOService;

<<<<<<< HEAD
    public static final String  UPLOAD_FILE = "/api/file/upload";
    public static final String  DOWNLOAD_FILE = "/api/file/download";
    public static final String  GET_FILES = "/api/file/get";

    @PostMapping(UPLOAD_FILE)
    public ResponseEntity<?> uploadFile(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile file){
        try{
            return minIOService.uploadFile(token, file);
=======
    @PostMapping(UPLOAD_FILE)
    public ResponseEntity<?> uploadFile(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("file") MultipartFile file){
        System.out.println(file);
        MultipartFile fl = file;

        try{
            return minIOService.uploadFile(token.substring(7), file);
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
        }
        catch (IOException e) {
            e.printStackTrace();

            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Ошибка при загрузке файла в MinIO"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (SignatureException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                    "Неверная подпись токена авторизации"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(DOWNLOAD_FILE + "/{filename}")
<<<<<<< HEAD
    public ResponseEntity<?> downloadFile(@RequestHeader("Authorization") String token, @PathVariable("filename") UUID fileId){
        try{
            return minIOService.downloadFile(token, fileId);
=======
    public ResponseEntity<?> downloadFile(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable("filename") UUID fileId){
        try{
            return minIOService.downloadFile(token.substring(7), fileId);
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
        }
        catch (SignatureException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                    "Неверная подпись токена авторизации"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Ошибка при скачивании файла из MinIO"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_FILES)
<<<<<<< HEAD
    public ResponseEntity<?> getFiles(@RequestHeader("Authorization") String token){
        try{
            return minIOService.getAllFiles(token);
=======
    public ResponseEntity<?> getFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        try{
           return minIOService.getAllFiles(token.substring(7));
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
        }
        catch (SignatureException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                    "Неверная подпись токена авторизации"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
