package com.hits.forum.Services;

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
    ResponseEntity<?> createCategory(User user, CategoryRequest createCategoryRequest);
    ResponseEntity<?> createTheme(User user, ThemeRequest createThemeRequest);
    ResponseEntity<?> createMessage(User user, MessageRequest createMessageRequest);
    ResponseEntity<?> editCategory(User user, UUID categoryId, CategoryRequest createCategoryRequest);
    ResponseEntity<?> editTheme(User user, UUID themeId, ThemeRequest createThemeRequest);
    ResponseEntity<?> editMessage(User user, UUID messageId, EditMessageRequest editMessageRequest);
    ResponseEntity<?> deleteCategory(User user, UUID categoryId);
    ResponseEntity<?> deleteTheme(User user, UUID themeId);
    ResponseEntity<?> deleteMessage(User user, UUID messageId);
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
