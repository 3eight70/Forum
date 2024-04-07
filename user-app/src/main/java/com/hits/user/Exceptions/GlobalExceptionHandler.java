package com.hits.user.Exceptions;

import com.hits.common.Core.Response.Response;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Запрос не является multipart запросом")
    public ResponseEntity<Response> handleMultipartException(MultipartException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Запрос не является multipart запросом"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Токен просрочен")
    public ResponseEntity<Response> handleExpiredJwtException(ExpiredJwtException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Структура токена нарушен")
    public ResponseEntity<Response> handleMalformedJwtException(MalformedJwtException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Структура jwt токена нарушена"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Подпись токена нарушена")
    public ResponseEntity<Response> handleSignatureException(SignatureException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                "Подпись jwt токена нарушена"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Токен просрочен")
    public ResponseEntity<Response> handleExpiredTokenException(ExpiredTokenException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleFeignExceptionUnauthorized(FeignException.Unauthorized e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Неправильный логин или пароль")
    public ResponseEntity<Response> handleBadCredentialsException(BadCredentialsException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Пользователь с указанным логином не найден")
    public ResponseEntity<Response> handleUsernameNotFoundException(UsernameNotFoundException e){
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                "Указанный логин не найден"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountNotConfirmedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Аккаунт не подтвержден")
    public ResponseEntity<Response> handleAccountNotConfirmedException(AccountNotConfirmedException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(responseCode = "401", description = "Токен невалиден")
    public ResponseEntity<Response> handleInvalidTokenException(InvalidTokenException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Что-то пошло не так")
    public ResponseEntity<Response> handleException(Exception e){
        log.error(e.getMessage(), e);
        Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
