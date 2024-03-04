package com.hits.forum.Services;

import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Enums.SortOrder;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IForumService {
    ResponseEntity<?> createCategory(String token, CategoryRequest createCategoryRequest);
    ResponseEntity<?> createTheme(String token, ThemeRequest createThemeRequest);
    ResponseEntity<?> createMessage(String token, MessageRequest createMessageRequest);
    ResponseEntity<?> editCategory(String token, UUID categoryId, CategoryRequest createCategoryRequest);
    ResponseEntity<?> editTheme(String token, UUID themeId, ThemeRequest createThemeRequest);
    ResponseEntity<?> editMessage(String token, UUID messageId, EditMessageRequest editMessageRequest);
    ResponseEntity<?> deleteCategory(String token, UUID categoryId);
    ResponseEntity<?> deleteTheme(String token, UUID themeId);
    ResponseEntity<?> deleteMessage(String token, UUID messageId);
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
