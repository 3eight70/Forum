package com.hits.forum.Exceptions;

import com.hits.common.Models.Response.Response;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ResponseEntity<Response> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getMessage();
        int status = HttpStatus.BAD_REQUEST.value();

        return new ResponseEntity<>(new Response(status, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMessageNotReadableException(HttpMessageNotReadableException e){
        Response response = new Response(HttpStatus.BAD_REQUEST.value(),
                "Отсутствует тело запроса");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Отсутствует необходимый параметр запроса"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleMissingRequestHeader(MissingRequestHeaderException e) {
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Отсутствует header Authorization"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleExpiredJwtException(ExpiredJwtException e) {
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
    }
}
