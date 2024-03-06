package com.hits.file.Controllers;


import com.hits.file.Models.Dto.Response.Response;
import com.hits.file.Services.IMinIOService;
import com.hits.user.Models.Entities.User;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> uploadFile(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file){
        try{
            return minIOService.uploadFile(user, file);
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
    public ResponseEntity<?> downloadFile(@AuthenticationPrincipal User user, @PathVariable("filename") UUID fileId){
        try{
            return minIOService.downloadFile(user, fileId);
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

    @GetMapping(GET_FILES)
    public ResponseEntity<?> getFiles(@AuthenticationPrincipal User user){
        try{
           return minIOService.getAllFiles(user);
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
