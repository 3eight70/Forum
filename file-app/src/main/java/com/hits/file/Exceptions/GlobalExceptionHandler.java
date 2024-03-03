package com.hits.file.Exceptions;

<<<<<<< HEAD
=======
import com.fasterxml.jackson.core.JsonParseException;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import com.hits.file.Models.Dto.Response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
<<<<<<< HEAD
=======
import org.springframework.web.multipart.MultipartException;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchKeyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleNoSuchKeyException(NoSuchKeyException e){
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                "Файла с данным id не существует"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleSignatureException(SignatureException e){
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Неверная подпись токена авторизации"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
<<<<<<< HEAD
=======
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
    public ResponseEntity<Response> handleMissingRequestHeader(MissingRequestHeaderException e) {
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Отсутствует header Authorization"), HttpStatus.UNAUTHORIZED);
    }
<<<<<<< HEAD
=======

    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleJsonParseException(JsonParseException e) {
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Не удалось получить данные с JSON"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMultipartException(MultipartException e) {
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Файл не был прикреплен"), HttpStatus.BAD_REQUEST);
    }
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
}
