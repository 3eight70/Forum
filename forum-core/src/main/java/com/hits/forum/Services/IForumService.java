package com.hits.forum.Services;

import com.hits.forum.Models.Dto.CategoryRequest;
import com.hits.forum.Models.Dto.EditMessageRequest;
import com.hits.forum.Models.Dto.MessageRequest;
import com.hits.forum.Models.Dto.ThemeRequest;
import org.springframework.http.ResponseEntity;

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
}
