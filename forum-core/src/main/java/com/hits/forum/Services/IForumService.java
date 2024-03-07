package com.hits.forum.Services;

import com.hits.common.Models.User.UserDto;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Enums.SortOrder;
import com.hits.user.Models.Entities.User;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IForumService {
    ResponseEntity<?> createCategory(UserDto user, CategoryRequest createCategoryRequest);
    ResponseEntity<?> createTheme(UserDto user, ThemeRequest createThemeRequest);
    ResponseEntity<?> createMessage(UserDto user, MessageRequest createMessageRequest);
    ResponseEntity<?> editCategory(UserDto user, UUID categoryId, CategoryRequest createCategoryRequest);
    ResponseEntity<?> editTheme(UserDto user, UUID themeId, ThemeRequest createThemeRequest);
    ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest);
    ResponseEntity<?> deleteCategory(UserDto user, UUID categoryId);
    ResponseEntity<?> deleteTheme(UserDto user, UUID themeId);
    ResponseEntity<?> deleteMessage(UserDto user, UUID messageId);
    ResponseEntity<?> getAllThemes(Integer page, Integer size, SortOrder sortOrder);
    ResponseEntity<?> getCategories(SortOrder sortOrder);
    ResponseEntity<?> getMessages(UUID themeId, Integer page, Integer size, SortOrder sortOrder);
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
}
