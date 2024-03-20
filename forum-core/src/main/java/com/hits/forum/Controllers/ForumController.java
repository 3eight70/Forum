package com.hits.forum.Controllers;

import com.hits.common.Client.ForumAppClient;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.User.UserDto;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Enums.SortOrder;
import com.hits.forum.Services.IForumService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class ForumController implements ForumAppClient {
    private final IForumService forumService;

    @PostMapping(CREATE_CATEGORY)
    public ResponseEntity<?> createCategory(@AuthenticationPrincipal UserDto user, @Valid @RequestBody CategoryRequest createCategoryRequest){

        try {
            return forumService.createCategory(user, createCategoryRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(CREATE_THEME)
    public ResponseEntity<?> createTheme(@AuthenticationPrincipal UserDto user, @Valid @RequestBody ThemeRequest createThemeRequest){

        try {
            return forumService.createTheme(user, createThemeRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(SEND_MESSAGE)
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal UserDto user, @Valid @RequestBody MessageRequest messageRequest){

        try {
            return forumService.createMessage(user, messageRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_CATEGORY)
    public ResponseEntity<?> editCategory(@AuthenticationPrincipal UserDto user, @RequestParam(name = "categoryId") UUID categoryId, @Valid @RequestBody CategoryRequest createCategoryRequest){

        try {
            return forumService.editCategory(user, categoryId, createCategoryRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_THEME)
    public ResponseEntity<?> editTheme(@AuthenticationPrincipal UserDto user, @RequestParam(name = "themeId") UUID themeId, @Valid @RequestBody ThemeRequest createThemeRequest){

        try {
            return forumService.editTheme(user, themeId, createThemeRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(EDIT_MESSAGE)
    public ResponseEntity<?> editMessage(@AuthenticationPrincipal UserDto user, @RequestParam(name = "messageId") UUID messageId, @Valid @RequestBody EditMessageRequest editMessageRequest){

        try {
            return forumService.editMessage(user, messageId, editMessageRequest);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal UserDto user, @RequestParam(name = "categoryId") UUID categoryId){

        try {
            return forumService.deleteCategory(user, categoryId);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_THEME)
    public ResponseEntity<?> deleteTheme(@AuthenticationPrincipal UserDto user, @RequestParam(name = "themeId") UUID themeId){

        try {
            return forumService.deleteTheme(user, themeId);
        }
        catch (ExpiredJwtException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Срок действия токена истек"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_MESSAGE)
    public ResponseEntity<?> deleteMessage(@AuthenticationPrincipal UserDto user, @RequestParam(name = "messageId") UUID messageId){

        try {
            return forumService.deleteMessage(user, messageId);
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
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateDesc") SortOrder sortOrder){
        try {
            return forumService.getAllThemes(page, size, sortOrder);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_CATEGORIES)
    public ResponseEntity<?> getCategories(@RequestParam(name = "sortOrder", required = false, defaultValue = "NameAsc") SortOrder sortOrder){
        try{
            return forumService.getCategories(sortOrder);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_MESSAGES)
    public ResponseEntity<?> getMessages(
            @PathVariable("themeId") UUID themeId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateDesc") SortOrder sortOrder){
        try {
            return forumService.getMessages(themeId, page, size, sortOrder);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_MESSAGES_WITH_FILTERS)
    public ResponseEntity<?> getMessagesWithFilters(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "timeFrom", required = false) LocalDateTime timeFrom,
            @RequestParam(value = "timeTo", required = false) LocalDateTime timeTo,
            @RequestParam(value = "authorLogin", required = false) String authorLogin,
            @RequestParam(value = "themeId", required = false) UUID themeId,
            @RequestParam(value = "categoryId", required = false) UUID categoryId
            ){
        try {
            return forumService.getMessagesWithFilters(content, timeFrom, timeTo, authorLogin, themeId, categoryId);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_CATEGORIES_WITH_SUBSTRING)
    public ResponseEntity<?> getCategoriesWithSubstring(@RequestParam(value = "name") String name){
        try {
            return forumService.getCategoriesWithSubstring(name);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_THEMES_WITH_SUBSTRING)
    public ResponseEntity<?> getThemesWithSubstring(@RequestParam(value = "name") String name){
        try {
            return forumService.getThemesWithSubstring(name);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_MESSAGES_WITH_SUBSTRING)
    public ResponseEntity<?> getMessagesWithSubstring(@RequestParam(value = "content") String content){
        try {
            return forumService.getMessagesWithSubstring(content);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> checkTheme(@RequestParam(name = "themeId") UUID themeId) {
        try {
            return forumService.checkTheme(themeId);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> checkCategory(@RequestParam(name = "themeId") UUID categoryId) {
        try {
            return forumService.checkCategory(categoryId);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getThemesById(@RequestParam(name = "themeId") List<UUID> themesId){
        try {
            return forumService.getThemesById(themesId);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
