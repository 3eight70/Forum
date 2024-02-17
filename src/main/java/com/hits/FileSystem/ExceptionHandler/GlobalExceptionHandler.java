package com.hits.FileSystem.ExceptionHandler;

import com.hits.FileSystem.Models.Dto.Response.Response;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMultipartException(MultipartException e){
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Запрос не является multipart запросом"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchKeyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleNoSuchKeyException(NoSuchKeyException e){
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                "Файла с данным id не существует"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleExpiredJwtException(ExpiredJwtException e) {
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
    }
}
