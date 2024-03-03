package com.hits.Controllers;

import com.hits.Models.Dto.CategoryRequest;
import com.hits.Models.Dto.ThemeRequest;
import com.hits.Models.Dto.MessageRequest;
import com.hits.common.Models.Response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ForumController {

    public static final String CREATE_CATEGORY = "/api/forum/category";
    public static final String CREATE_THEME = "/api/forum/category/theme";
    public static final String SEND_MESSAGE = "/api/forum/theme/message";
    public static final String EDIT_CATEGORY = "/api/forum/category/edit";
    public static final String EDIT_THEME = "/api/forum/category/theme/edit";
    public static final String EDIT_MESSAGE = "/api/forum/theme/message/edit";
    public static final String DELETE_CATEGORY = "/api/forum/category/delete";
    public static final String DELETE_THEME = "/api/forum/category/theme/delete";
    public static final String DELETE_MESSAGE = "/api/forum/theme/message/delete";

    @PostMapping(CREATE_CATEGORY)
    public ResponseEntity<?> createCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, CategoryRequest createCategoryRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(CREATE_THEME)
    public ResponseEntity<?> createTheme(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, ThemeRequest createThemeRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(SEND_MESSAGE)
    public ResponseEntity<?> sendMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, MessageRequest messageRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_CATEGORY)
    public ResponseEntity<?> editCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, CategoryRequest createCategoryRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_THEME)
    public ResponseEntity<?> editTheme(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, ThemeRequest createThemeRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_MESSAGE)
    public ResponseEntity<?> editMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, MessageRequest messageRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<?> deleteCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, CategoryRequest createCategoryRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_THEME)
    public ResponseEntity<?> deleteTheme(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, ThemeRequest createThemeRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_MESSAGE)
    public ResponseEntity<?> deleteMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, MessageRequest messageRequest){

        try {
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
