package com.hits.user.Exceptions;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.Response.Response;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.naming.ServiceUnavailableException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? "поле '" + fieldError.getField() + "' " + fieldError.getDefaultMessage() : "Ошибка валидации";
        int status = HttpStatus.BAD_REQUEST.value();
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(new Response(status, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMultipartException(MultipartException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Запрос не является multipart запросом"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleExpiredJwtException(ExpiredJwtException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleFeignExceptionUnauthorized(FeignException.Unauthorized e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleBadCredentialsExceptionAndUsernameNotFoundException(BadCredentialsException e, UsernameNotFoundException e1){
        String errorMessage = e != null ? e.getMessage() : e1.getMessage();
        log.error(errorMessage, e != null ? e : e1);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(AccountNotConfirmedException.class)
    public ResponseEntity<Response> handleAccountNotConfirmedException(AccountNotConfirmedException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Response> handleInvalidTokenException(InvalidTokenException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e){
        log.error(e.getMessage(), e);
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
