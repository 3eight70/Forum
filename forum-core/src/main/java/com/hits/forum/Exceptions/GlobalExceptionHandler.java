package com.hits.forum.Exceptions;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.common.Models.Response.Response;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Ошибка валидации";

        int status = HttpStatus.BAD_REQUEST.value();
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(new Response(status, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMessageNotReadableException(HttpMessageNotReadableException e){
        log.error(e.getMessage(), e);
        Response response = new Response(HttpStatus.BAD_REQUEST.value(),
                "Отсутствует тело запроса");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleExpiredJwtException(ExpiredJwtException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
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

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<Response> handleObjectAlreadyExistsException(ObjectAlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
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
