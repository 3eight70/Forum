package com.hits.forum.Services;

import com.hits.common.Models.Response.Response;
import com.hits.common.Utils.JwtUtils;
import com.hits.forum.Mappers.ForumMapper;
import com.hits.forum.Models.Dto.CategoryRequest;
import com.hits.forum.Models.Dto.EditMessageRequest;
import com.hits.forum.Models.Dto.MessageRequest;
import com.hits.forum.Models.Dto.ThemeRequest;
import com.hits.forum.Models.Entities.ForumCategory;
import com.hits.forum.Models.Entities.ForumMessage;
import com.hits.forum.Models.Entities.ForumTheme;
import com.hits.forum.Repositories.CategoryRepository;
import com.hits.forum.Repositories.MessageRepository;
import com.hits.forum.Repositories.ThemeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ForumService implements IForumService {
    private final CategoryRepository categoryRepository;
    private final ThemeRepository themeRepository;
    private final MessageRepository messageRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Transactional
    public ResponseEntity<?> createCategory(String token, CategoryRequest createCategoryRequest) {
        ForumCategory forumCategory = categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());

        if (forumCategory != null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория с данным названием уже существует"), HttpStatus.BAD_REQUEST);
        }

        if (createCategoryRequest.getParentId() != null) {
            forumCategory = categoryRepository.findForumCategoryById(createCategoryRequest.getParentId());

            if (forumCategory == null) {
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
            }
        }

        forumCategory = ForumMapper.categoryRequestToForumCategory(JwtUtils.getUserLogin(token, secret), createCategoryRequest);

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createTheme(String token, ThemeRequest createThemeRequest) {
        ForumTheme forumTheme = themeRepository.findByThemeNameAndCategoryId(createThemeRequest.getThemeName(), createThemeRequest.getCategoryId());

        if (forumTheme != null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "В данной категории уже существует тема с данным названием"), HttpStatus.BAD_REQUEST);
        }
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(createThemeRequest.getCategoryId());

        if (forumCategory == null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
        }

        forumTheme = ForumMapper.themeRequestToForumTheme(JwtUtils.getUserLogin(token, secret), createThemeRequest);

        themeRepository.saveAndFlush(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createMessage(String token, MessageRequest createMessageRequest) {
        ForumTheme forumTheme = themeRepository.findForumThemeById(createMessageRequest.getThemeId());

        if (forumTheme == null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Тема-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
        }

        ForumMessage forumMessage = ForumMapper.messageRequestToForumTheme(JwtUtils.getUserLogin(token, secret), createMessageRequest);

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно создано"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editCategory(String token, UUID categoryId, CategoryRequest createCategoryRequest) {
        UUID parentId = createCategoryRequest.getParentId();

        if (parentId != null && parentId.equals(categoryId)) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория не может быть родителем для самой себя"), HttpStatus.BAD_REQUEST);
        }
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);


        if (forumCategory == null) {
            return notFoundResponse("Категории с данным id не существует");
        }

        if (!Objects.equals(JwtUtils.getUserLogin(token, secret), forumCategory.getAuthorLogin())) {
            return forbiddenResponse();
        }

        if (parentId != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(parentId);


            if (parent == null) {
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
            } else {
                UUID parentOfParentId = parent.getParentId();
                if (parentOfParentId != null && parentOfParentId.equals(categoryId)) {
                    return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                            "Категории не могут одновременно быть родителями друг друга"), HttpStatus.BAD_REQUEST);
                }
            }
        }

        ForumCategory checkCategory = categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());

        if (checkCategory != null && !Objects.equals(forumCategory.getCategoryName(), checkCategory.getCategoryName())) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория с указанным названием уже существует"), HttpStatus.BAD_REQUEST);
        }

        forumCategory.setParentId(parentId);
        forumCategory.setCategoryName(createCategoryRequest.getCategoryName());
        forumCategory.setModifiedTime(LocalDateTime.now());

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно изменена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editTheme(String token, UUID themeId, ThemeRequest createThemeRequest) {
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            return notFoundResponse("Темы с данным id не существует");
        }

        if (!Objects.equals(JwtUtils.getUserLogin(token, secret), forumTheme.getAuthorLogin())) {
            return forbiddenResponse();
        }

        ForumCategory forumCategory = categoryRepository.findForumCategoryById(createThemeRequest.getCategoryId());

        if (forumCategory != null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
        }

        ForumTheme checkTheme = themeRepository.findByThemeNameAndCategoryId(createThemeRequest.getThemeName(), createThemeRequest.getCategoryId());

        if (checkTheme != null && !Objects.equals(forumTheme.getThemeName(), checkTheme.getThemeName())
                && checkTheme.getCategoryId() != forumTheme.getCategoryId()) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Тема с указанным названием в данной категории уже существует"), HttpStatus.BAD_REQUEST);
        }

        forumTheme.setThemeName(createThemeRequest.getThemeName());
        forumTheme.setCategoryId(createThemeRequest.getCategoryId());
        forumTheme.setModifiedTime(LocalDateTime.now());

        themeRepository.saveAndFlush(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно изменена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editMessage(String token, UUID messageId, EditMessageRequest editMessageRequest) {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            return notFoundResponse("Сообщения с данным id не существует");
        }

        if (!Objects.equals(JwtUtils.getUserLogin(token, secret), forumMessage.getAuthorLogin())) {
            return forbiddenResponse();
        }


        forumMessage.setModifiedTime(LocalDateTime.now());
        forumMessage.setContent(editMessageRequest.getContent());

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно изменено"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(String token, UUID categoryId) {
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null) {
            return notFoundResponse("Категории с данным id не существует");
        }

        if (!Objects.equals(JwtUtils.getUserLogin(token, secret), forumCategory.getAuthorLogin())) {
            return forbiddenResponse();
        }

        categoryRepository.delete(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteTheme(String token, UUID themeId) {
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            return notFoundResponse("Темы с данным id не существует");
        }

        if (!Objects.equals(JwtUtils.getUserLogin(token, secret), forumTheme.getAuthorLogin())) {
            return new ResponseEntity<>(new Response(HttpStatus.FORBIDDEN.value(),
                    "Изменение чужих данных запрещено"), HttpStatus.FORBIDDEN);
        }

        themeRepository.delete(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteMessage(String token, UUID messageId) {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            return notFoundResponse("Сообщения с данным id не существует");
        }

        if (!Objects.equals(JwtUtils.getUserLogin(token, secret), forumMessage.getAuthorLogin())) {
            return new ResponseEntity<>(new Response(HttpStatus.FORBIDDEN.value(),
                    "Изменение чужих данных запрещено"), HttpStatus.FORBIDDEN);
        }

        messageRepository.delete(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно удалено"), HttpStatus.OK);
    }

    private ResponseEntity<?> notFoundResponse(String message) {
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(),
                message), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> forbiddenResponse() {
        return new ResponseEntity<>(new Response(HttpStatus.FORBIDDEN.value(),
                "Изменение чужих данных запрещено"), HttpStatus.FORBIDDEN);
    }
}
