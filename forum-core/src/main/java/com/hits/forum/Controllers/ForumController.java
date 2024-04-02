package com.hits.forum.Controllers;

import com.hits.security.Client.ForumAppClient;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.common.Models.User.UserDto;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Enums.SortOrder;
import com.hits.forum.Services.IForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class ForumController implements ForumAppClient {
    private final IForumService forumService;

    @PostMapping(CREATE_CATEGORY)
    public ResponseEntity<?> createCategory(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody CategoryRequest createCategoryRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException {
        return forumService.createCategory(user, createCategoryRequest);
    }

    @PostMapping(CREATE_THEME)
    public ResponseEntity<?> createTheme(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody ThemeRequest createThemeRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException{
        return forumService.createTheme(user, createThemeRequest);
    }

    @PostMapping(SEND_MESSAGE)
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "themeId") String themeId,
            @RequestParam(name = "file", required = false) List<MultipartFile> files)
            throws NotFoundException, IOException {
        return forumService.createMessage(user, content,UUID.fromString(themeId), files);
    }

    @PutMapping(EDIT_CATEGORY)
    public ResponseEntity<?> editCategory(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "categoryId") UUID categoryId,
            @Valid @RequestBody CategoryRequest createCategoryRequest)
            throws BadRequestException, NotFoundException, ForbiddenException {
        return forumService.editCategory(user, categoryId, createCategoryRequest);
    }

    @PutMapping(EDIT_THEME)
    public ResponseEntity<?> editTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "themeId") UUID themeId,
            @Valid @RequestBody ThemeRequest createThemeRequest)
            throws BadRequestException, NotFoundException, ForbiddenException
    {
        return forumService.editTheme(user, themeId, createThemeRequest);
    }

    @PutMapping(EDIT_MESSAGE)
    public ResponseEntity<?> editMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "messageId") UUID messageId,
            @Valid @RequestBody EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException{
            return forumService.editMessage(user, messageId, editMessageRequest);
    }
    @DeleteMapping(DELETE_CATEGORY)
    public ResponseEntity<?> deleteCategory(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "categoryId") UUID categoryId)
            throws NotFoundException, ForbiddenException{
        return forumService.deleteCategory(user, categoryId);
    }

    @DeleteMapping(DELETE_THEME)
    public ResponseEntity<?> deleteTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "themeId") UUID themeId)
            throws NotFoundException, ForbiddenException{
        return forumService.deleteTheme(user, themeId);
    }

    @DeleteMapping(DELETE_MESSAGE)
    public ResponseEntity<?> deleteMessage(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "messageId") UUID messageId)
            throws NotFoundException, ForbiddenException{
            return forumService.deleteMessage(user, messageId);
    }

    @GetMapping(GET_THEMES)
    public ResponseEntity<?> getThemes(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateDesc") SortOrder sortOrder){
        return forumService.getAllThemes(page, size, sortOrder);
    }

    @GetMapping(GET_CATEGORIES)
    public ResponseEntity<?> getCategories(
            @RequestParam(name = "sortOrder", required = false, defaultValue = "NameAsc") SortOrder sortOrder){
        return forumService.getCategories(sortOrder);
    }

    @GetMapping(GET_MESSAGES)
    public ResponseEntity<?> getMessages(
            @PathVariable("themeId") UUID themeId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateDesc") SortOrder sortOrder)
            throws NotFoundException{
        return forumService.getMessages(themeId, page, size, sortOrder);
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
        return forumService.getMessagesWithFilters(content, timeFrom, timeTo, authorLogin, themeId, categoryId);
    }

    @GetMapping(GET_CATEGORIES_WITH_SUBSTRING)
    public ResponseEntity<?> getCategoriesWithSubstring(@RequestParam(value = "name") String name){
        return forumService.getCategoriesWithSubstring(name);
    }

    @GetMapping(GET_THEMES_WITH_SUBSTRING)
    public ResponseEntity<?> getThemesWithSubstring(@RequestParam(value = "name") String name){
        return forumService.getThemesWithSubstring(name);
    }

    @GetMapping(GET_MESSAGES_WITH_SUBSTRING)
    public ResponseEntity<?> getMessagesWithSubstring(@RequestParam(value = "content") String content){
        return forumService.getMessagesWithSubstring(content);
    }

    @PostMapping(ARCHIVE_THEME)
    ResponseEntity<?> archiveTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(value = "themeId") UUID themeId)
            throws NotFoundException, ForbiddenException {
        return forumService.archiveTheme(user, themeId);
    }

    @DeleteMapping(ARCHIVE_THEME)
    ResponseEntity<?> unArchiveTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(value = "themeId") UUID themeId)
            throws NotFoundException, ForbiddenException {
        return forumService.unArchiveTheme(user, themeId);
    }

    @Override
    public ResponseEntity<?> checkTheme(@RequestParam(name = "themeId") UUID themeId)
            throws NotFoundException{
        return forumService.checkTheme(themeId);
    }

    @Override
    public ResponseEntity<?> checkCategory(@RequestParam(name = "categoryId") UUID categoryId)
            throws NotFoundException{
        return forumService.checkCategory(categoryId);
    }

    @GetMapping(GET_THEMES_BY_ID)
    public ResponseEntity<?> getThemesById(@RequestParam(name = "themeId") List<UUID> themesId){
        return forumService.getThemesById(themesId);
    }
}
