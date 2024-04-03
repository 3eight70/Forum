package com.hits.forum.Services;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.common.Models.Message.MessageDto;
import com.hits.common.Models.User.UserDto;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Enums.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IForumService {
    ResponseEntity<?> createCategory(UserDto user, CategoryRequest createCategoryRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException;

    ResponseEntity<?> createTheme(UserDto user, ThemeRequest createThemeRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException;

    ResponseEntity<?> createMessage(UserDto user, String content, UUID themeId, List<MultipartFile> files)
            throws NotFoundException, IOException;

    ResponseEntity<?> editCategory(UserDto user, UUID categoryId, CategoryRequest createCategoryRequest)
            throws BadRequestException, NotFoundException, ForbiddenException;

    ResponseEntity<?> editTheme(UserDto user, UUID themeId, ThemeRequest createThemeRequest)
            throws BadRequestException, NotFoundException, ForbiddenException;

    ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest)
            throws NotFoundException, ForbiddenException;

    ResponseEntity<?> deleteCategory(UserDto user, UUID categoryId)
            throws NotFoundException, ForbiddenException;

    ResponseEntity<?> deleteTheme(UserDto user, UUID themeId)
            throws NotFoundException, ForbiddenException;

    ResponseEntity<?> deleteMessage(UserDto user, UUID messageId)
            throws NotFoundException, ForbiddenException;

    ResponseEntity<?> getAllThemes(Integer page, Integer size, SortOrder sortOrder);
    ResponseEntity<?> getCategories(SortOrder sortOrder);

    ResponseEntity<?> getMessages(UUID themeId, Integer page, Integer size, SortOrder sortOrder)
            throws NotFoundException;

    ResponseEntity<?> getMessagesWithFilters(
            String content,
            LocalDateTime timeFrom,
            LocalDateTime timeTo,
            String authorLogin,
            UUID themeId,
            UUID categoryId);

    ResponseEntity<?> getCategoriesWithSubstring(String substring);
    ResponseEntity<?> getThemesWithSubstring(String substring);
    ResponseEntity<?> getMessagesWithSubstring(String substring);
    ResponseEntity<?> checkTheme(UUID themeId) throws NotFoundException;
    ResponseEntity<?> checkCategory(UUID categoryId) throws NotFoundException;
    ResponseEntity<?> getThemesById(List<UUID> themesId);
    ResponseEntity<?> archiveTheme(UserDto user, UUID themeId) throws NotFoundException, ForbiddenException;
    ResponseEntity<?> unArchiveTheme(UserDto user, UUID themeId) throws NotFoundException, ForbiddenException;
    ResponseEntity<MessageDto> checkMessage(UUID messageId) throws NotFoundException;
}
