package com.hits.gateway.Exceptions;

import com.hits.common.Models.Response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
