package com.hits.forum.Services;

import com.hits.common.Models.Response.Response;
import com.hits.forum.Mappers.ForumMapper;
import com.hits.forum.Models.Dto.Category.CategoryDto;
import com.hits.forum.Models.Dto.Category.CategoryRequest;
import com.hits.forum.Models.Dto.Category.CategoryWithSubstring;
import com.hits.forum.Models.Dto.Message.EditMessageRequest;
import com.hits.forum.Models.Dto.Message.MessageDto;
import com.hits.forum.Models.Dto.Message.MessageRequest;
import com.hits.forum.Models.Dto.Message.MessageWithFiltersDto;
import com.hits.forum.Models.Dto.Responses.MessageResponse;
import com.hits.forum.Models.Dto.Responses.PageResponse;
import com.hits.forum.Models.Dto.Responses.ThemeResponse;
import com.hits.forum.Models.Dto.Theme.ThemeDto;
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Entities.ForumCategory;
import com.hits.forum.Models.Entities.ForumMessage;
import com.hits.forum.Models.Entities.ForumTheme;
import com.hits.forum.Models.Enums.SortOrder;
import com.hits.forum.Repositories.CategoryRepository;
import com.hits.forum.Repositories.MessageRepository;
import com.hits.forum.Repositories.ThemeRepository;
import com.hits.user.Models.Entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ForumService implements IForumService {
    private final CategoryRepository categoryRepository;
    private final ThemeRepository themeRepository;
    private final MessageRepository messageRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Transactional
    public ResponseEntity<?> createCategory(User user, CategoryRequest createCategoryRequest) {
        ForumCategory forumCategory = categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());

        if (forumCategory != null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория с данным названием уже существует"), HttpStatus.BAD_REQUEST);
        }

        forumCategory = ForumMapper.categoryRequestToForumCategory(user.getLogin(), createCategoryRequest);

        if (createCategoryRequest.getParentId() != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(createCategoryRequest.getParentId());

            if (parent == null) {
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
            }

            if (!parent.getThemes().isEmpty()){
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "У данной категории уже присутствуют топики"), HttpStatus.BAD_REQUEST);
            }
            parent.getChildCategories().add(forumCategory);
            categoryRepository.saveAndFlush(parent);
        }

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createTheme(User user, ThemeRequest createThemeRequest) {
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
        else if (!forumCategory.getChildCategories().isEmpty()){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Вы не можете создать топик не в категории нижнего уровня"), HttpStatus.BAD_REQUEST);
        }

        forumTheme = ForumMapper.themeRequestToForumTheme(user.getLogin(), createThemeRequest);

        themeRepository.saveAndFlush(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createMessage(User user, MessageRequest createMessageRequest) {
        ForumTheme forumTheme = themeRepository.findForumThemeById(createMessageRequest.getThemeId());

        if (forumTheme == null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Тема-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
        }

        ForumMessage forumMessage = ForumMapper.messageRequestToForumTheme(user.getLogin(), createMessageRequest, forumTheme.getCategoryId());

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно создано"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editCategory(User user, UUID categoryId, CategoryRequest createCategoryRequest) {
        UUID parentId = createCategoryRequest.getParentId();

        if (parentId != null && parentId.equals(categoryId)) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория не может быть родителем для самой себя"), HttpStatus.BAD_REQUEST);
        }
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);


        if (forumCategory == null) {
            return notFoundResponse("Категории с данным id не существует");
        }

        if (!Objects.equals(user.getLogin(), forumCategory.getAuthorLogin())) {
            return forbiddenResponse();
        }

        if (parentId != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(parentId);


            if (parent == null) {
                return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                        "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
            } else {
                if (!parent.getThemes().isEmpty()){
                    return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                            "У данной категории уже присутствуют топики"), HttpStatus.BAD_REQUEST);
                }
                UUID parentOfParentId = parent.getParentId();
                if (parentOfParentId != null && parentOfParentId.equals(categoryId)) {
                    return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                            "Категории не могут одновременно быть родителями друг друга"), HttpStatus.BAD_REQUEST);
                }

                parent.getChildCategories().add(forumCategory);
                categoryRepository.saveAndFlush(parent);
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
    public ResponseEntity<?> editTheme(User user, UUID themeId, ThemeRequest createThemeRequest) {
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            return notFoundResponse("Темы с данным id не существует");
        }

        if (!Objects.equals(user.getLogin(), forumTheme.getAuthorLogin())) {
            return forbiddenResponse();
        }

        ForumCategory forumCategory = categoryRepository.findForumCategoryById(createThemeRequest.getCategoryId());

        if (forumCategory != null) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Категория-родитель с указанным id не существует"), HttpStatus.BAD_REQUEST);
        }
        else if (!forumCategory.getChildCategories().isEmpty()){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(),
                    "Вы не можете создать топик не в категории нижнего уровня"), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> editMessage(User user, UUID messageId, EditMessageRequest editMessageRequest) {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            return notFoundResponse("Сообщения с данным id не существует");
        }

        if (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin())) {
            return forbiddenResponse();
        }


        forumMessage.setModifiedTime(LocalDateTime.now());
        forumMessage.setContent(editMessageRequest.getContent());

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно изменено"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(User user, UUID categoryId) {
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null) {
            return notFoundResponse("Категории с данным id не существует");
        }

        if (!Objects.equals(user.getLogin(), forumCategory.getAuthorLogin())) {
            return forbiddenResponse();
        }

        categoryRepository.delete(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteTheme(User user, UUID themeId) {
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            return notFoundResponse("Темы с данным id не существует");
        }

        if (!Objects.equals(user.getLogin(), forumTheme.getAuthorLogin())) {
            return new ResponseEntity<>(new Response(HttpStatus.FORBIDDEN.value(),
                    "Изменение чужих данных запрещено"), HttpStatus.FORBIDDEN);
        }

        themeRepository.delete(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteMessage(User user, UUID messageId) {
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            return notFoundResponse("Сообщения с данным id не существует");
        }

        if (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin())) {
            return new ResponseEntity<>(new Response(HttpStatus.FORBIDDEN.value(),
                    "Изменение чужих данных запрещено"), HttpStatus.FORBIDDEN);
        }

        messageRepository.delete(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно удалено"), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllThemes(Integer page, Integer size, SortOrder sortOrder){
        Sort sort = Sort.by(getComparator(sortOrder));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ForumTheme> themesPage = themeRepository.findAll(pageable);

        List<ThemeDto> themeRequests = themesPage.getContent().stream()
                .map(ForumMapper::forumThemeToThemeDto)
                .collect(Collectors.toList());

        Page<ThemeDto> themeRequestsPage = new PageImpl<>(themeRequests, pageable, themesPage.getTotalElements());
        Long totalThemes = themeRepository.count();

        Integer totalPages = (int) Math.ceil((double) totalThemes/ size);

        return ResponseEntity.ok(new ThemeResponse(
                themeRequestsPage.getContent(),
                new PageResponse(
                        totalPages,
                        page,
                        themeRequestsPage.getSize(),
                        totalThemes
                )
        ));
    }

    public ResponseEntity<?> getCategories(SortOrder sortOrder){
        Sort sort = Sort.by(getComparator(sortOrder));
        List<CategoryDto> categories = categoryRepository.findAllByParentIdIsNull(sort)
                .stream()
                .map(ForumMapper::forumCategoryToCategoryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<?> getMessages(UUID themeId, Integer page, Integer size, SortOrder sortOrder){
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            return notFoundResponse("Темы с данным id не существует");
        }

        Sort sort = Sort.by(getComparator(sortOrder));
        Pageable pageable = PageRequest.of(page, size, sort);

        List<MessageDto> messageDtos = messageRepository.findAllByThemeId(themeId, pageable)
                .stream()
                .map(ForumMapper::forumMessageToMessageDto)
                .collect(Collectors.toList());

        Page<MessageDto> messageDtoPage = new PageImpl<>(messageDtos, pageable, messageDtos.size());

        Long totalThemes = messageRepository.countAllByThemeId(themeId);

        Integer totalPages = (int) Math.ceil((double) totalThemes/ size);

        return ResponseEntity.ok(new MessageResponse(
                messageDtoPage.getContent(),
                new PageResponse(
                        totalPages,
                        page,
                        messageDtoPage.getSize(),
                        totalThemes
                )
        ));
    }

    public ResponseEntity<?> getMessagesWithFilters(String content,
                                                    LocalDateTime timeFrom,
                                                    LocalDateTime timeTo,
                                                    String authorLogin,
                                                    UUID themeId,
                                                    UUID categoryId){


        List<MessageWithFiltersDto> messages = messageRepository.findAll()
                .stream()
                .filter(m -> content == null || m.getContent().contains(content))
                .filter(m -> timeFrom == null || m.getCreateTime().isAfter(timeFrom))
                .filter(m -> timeTo == null || m.getCreateTime().isBefore(timeTo))
                .filter(m -> authorLogin == null || m.getAuthorLogin().contains(authorLogin))
                .filter(m -> themeId == null || m.getThemeId().equals(themeId))
                .filter(m -> categoryId == null || m.getCategoryId().equals(categoryId))
                .map(ForumMapper::forumMessageToMessageWithFiltersDto)
                .toList();

        return ResponseEntity.ok(messages);
    }

    public ResponseEntity<?> getCategoriesWithSubstring(String substring){
        List<CategoryWithSubstring> forumCategories = categoryRepository.findAllByCategoryNameContainingIgnoreCase(substring)
                .stream()
                .map(ForumMapper::forumCategoryToCategoryWithSubstring)
                .toList();

        return ResponseEntity.ok(forumCategories);
    }

    public ResponseEntity<?> getThemesWithSubstring(String substring){
        List<ThemeDto> forumThemes = themeRepository.findAllByThemeNameContainingIgnoreCase(substring)
                .stream()
                .map(ForumMapper::forumThemeToThemeDto)
                .toList();

        return ResponseEntity.ok(forumThemes);
    }

    public ResponseEntity<?> getMessagesWithSubstring(String substring){
        List<MessageWithFiltersDto> forumMessages = messageRepository.findAllByContentContainingIgnoreCase(substring)
                .stream()
                .map(ForumMapper::forumMessageToMessageWithFiltersDto)
                .toList();

        return ResponseEntity.ok(forumMessages);
    }

    private Sort.Order getComparator(SortOrder sortOrder) {
        return switch (sortOrder) {
            case CreateDesc -> Sort.Order.desc("createTime");
            case CreateAsc -> Sort.Order.asc("createTime");
            case NameAsc -> Sort.Order.asc("categoryName");
            case NameDesc -> Sort.Order.desc("categoryName");
        };
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
