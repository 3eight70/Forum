package com.hits.file.Exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.hits.common.Core.Response.Response;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.FileLimitException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;
import java.security.SignatureException;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchKeyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleNoSuchKeyException(NoSuchKeyException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                "Файла с данным id не существует"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleSignatureException(SignatureException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Неверная подпись токена авторизации"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileLimitException.class)
    public ResponseEntity<Response> handleFileLimitException(FileLimitException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Загружайте не более 5 файлов"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Отсутствует необходимый параметр запроса"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleMissingRequestHeader(MissingRequestHeaderException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Отсутствует header Authorization"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleJsonParseException(JsonParseException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Не удалось получить данные с JSON"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMultipartException(MultipartException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Response> handleIOException(IOException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response> handleForbiddenException(ForbiddenException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.FORBIDDEN.value(),
                "У вас нет прав доступа"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e){
        log.error(e.getMessage(), e);
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
