package com.hits.file.Controllers;


import com.hits.file.Models.Dto.Response.Response;
import com.hits.file.Services.IMinIOService;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class MinIOController {
    private final IMinIOService minIOService;

    @PostMapping(UPLOAD_FILE)
    public ResponseEntity<?> uploadFile(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("file") MultipartFile file){
        try{
            return minIOService.uploadFile(token.substring(7), file);
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
    public ResponseEntity<?> downloadFile(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable("filename") UUID fileId){
        try{
            return minIOService.downloadFile(token.substring(7), fileId);
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
    public ResponseEntity<?> getFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        try{
           return minIOService.getAllFiles(token.substring(7));
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
