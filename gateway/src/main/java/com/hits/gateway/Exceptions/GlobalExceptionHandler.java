package com.hits.gateway.Exceptions;

import com.hits.common.Models.Response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.UnknownHostException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<Response> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        Response response = new Response(HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Сервис недоступен");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UnknownHostException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleUnknownHostException(UnknownHostException e) {
        log.error(e.getMessage(), e);
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Неизвестное имя хоста");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
