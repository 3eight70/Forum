package com.hits.forum.Controllers;

import com.hits.common.Models.Response.Response;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Enums.SortOrder;
import com.hits.forum.Services.IForumService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class ForumController {
    private final IForumService forumService;

    @PostMapping(CREATE_CATEGORY)
    public ResponseEntity<?> createCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @Valid @RequestBody CategoryRequest createCategoryRequest){

        try {
            return forumService.createCategory(token.substring(7), createCategoryRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(CREATE_THEME)
    public ResponseEntity<?> createTheme(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @Valid @RequestBody ThemeRequest createThemeRequest){

        try {
            return forumService.createTheme(token.substring(7), createThemeRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(SEND_MESSAGE)
    public ResponseEntity<?> sendMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @Valid @RequestBody MessageRequest messageRequest){

        try {
            return forumService.createMessage(token.substring(7), messageRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_CATEGORY)
    public ResponseEntity<?> editCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "categoryId") UUID categoryId, @Valid @RequestBody CategoryRequest createCategoryRequest){

        try {
            return forumService.editCategory(token.substring(7), categoryId, createCategoryRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_THEME)
    public ResponseEntity<?> editTheme(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "themeId") UUID themeId, @Valid @RequestBody ThemeRequest createThemeRequest){

        try {
            return forumService.editTheme(token.substring(7), themeId, createThemeRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_MESSAGE)
    public ResponseEntity<?> editMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "messageId") UUID messageId, @Valid @RequestBody EditMessageRequest editMessageRequest){

        try {
            return forumService.editMessage(token.substring(7), messageId, editMessageRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<?> deleteCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "categoryId") UUID categoryId){

        try {
            return forumService.deleteCategory(token.substring(7), categoryId);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_THEME)
    public ResponseEntity<?> deleteTheme(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "themeId") UUID themeId){

        try {
            return forumService.deleteTheme(token.substring(7), themeId);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_MESSAGE)
    public ResponseEntity<?> deleteMessage(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(name = "messageId") UUID messageId){

        try {
            return forumService.deleteMessage(token.substring(7), messageId);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_THEMES)
    public ResponseEntity<?> getThemes(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateAsc") SortOrder sortOrder){
        try {
            return forumService.getAllThemes(page, size, sortOrder);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
