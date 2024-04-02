package com.hits.forum.Services;

import com.hits.common.Exceptions.*;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.Theme.ThemeDto;
import com.hits.common.Models.User.Role;
import com.hits.common.Models.User.UserDto;
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
import com.hits.forum.Models.Dto.Theme.ThemeRequest;
import com.hits.forum.Models.Entities.File;
import com.hits.forum.Models.Entities.ForumCategory;
import com.hits.forum.Models.Entities.ForumMessage;
import com.hits.forum.Models.Entities.ForumTheme;
import com.hits.forum.Models.Enums.SortOrder;
import com.hits.forum.Repositories.CategoryRepository;
import com.hits.forum.Repositories.FileRepository;
import com.hits.forum.Repositories.MessageRepository;
import com.hits.forum.Repositories.ThemeRepository;
import com.hits.security.Client.FileAppClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Qualifier("com.hits.security.Client.FileAppClient")
    private final FileAppClient fileAppClient;
    private final FileRepository fileRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Transactional
    public ResponseEntity<?> createCategory(UserDto user, CategoryRequest createCategoryRequest)
    throws ObjectAlreadyExistsException, NotFoundException, BadRequestException{
        String categoryName = createCategoryRequest.getCategoryName();
        ForumCategory forumCategory = categoryRepository.findByCategoryName(categoryName);

        if (forumCategory != null) {
           throw new ObjectAlreadyExistsException(String.format("Категория с названием=%s уже существует", categoryName));
        }

        forumCategory = ForumMapper.categoryRequestToForumCategory(user.getLogin(), createCategoryRequest);

        UUID parentId = createCategoryRequest.getParentId();
        if (parentId != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(createCategoryRequest.getParentId());

            if (parent == null) {
                throw new NotFoundException(String.format("Категория-родитель с id=%s не существует", parentId));
            }

            if (!parent.getThemes().isEmpty()){
                throw new BadRequestException("У данной категории уже присутствуют топики");
            }
            parent.getChildCategories().add(forumCategory);
            categoryRepository.saveAndFlush(parent);
        }

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createTheme(UserDto user, ThemeRequest createThemeRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException {
        String themeName = createThemeRequest.getThemeName();
        UUID categoryId = createThemeRequest.getCategoryId();
        ForumTheme forumTheme = themeRepository.findByThemeNameAndCategoryId(themeName, categoryId);

        if (forumTheme != null) {
            throw new ObjectAlreadyExistsException(String.format("В данной категории уже существует тема с названием=%s", themeName));
        }
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null) {
            throw new NotFoundException(String.format("Категория-родитель с id=%s не существует", categoryId));
        }
        else if (!forumCategory.getChildCategories().isEmpty()){
           throw new BadRequestException("Вы не можете создать топик не в категории нижнего уровня");
        }

        forumTheme = ForumMapper.themeRequestToForumTheme(user.getLogin(), createThemeRequest);

        themeRepository.saveAndFlush(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно создана"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> createMessage(UserDto user, String content, UUID themeId, List<MultipartFile> files)
            throws NotFoundException, IOException, BadRequestException {
        if (files != null && files.size() > 5){
            throw new FileLimitException();
        }

        if (content.isEmpty()){
            throw new BadRequestException("Минимальная длина сообщения равна 1");
        }

        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Тема-родитель с id=%s не существует", themeId));
        }
        else if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        ForumMessage forumMessage = ForumMapper.messageRequestToForumTheme(user.getLogin(), content,
                themeId, forumTheme.getCategoryId());
        UUID messageId = forumMessage.getId();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                UUID fileId = fileAppClient.uploadFile(messageId, file); //выкидывает Failed to parse multipart servlet request
                File currentFile = ForumMapper.multipartFileToFile(file, fileId);
                fileRepository.save(currentFile);
                forumMessage.getFiles().add(currentFile);
            }
        }

        messageRepository.saveAndFlush(forumMessage);


        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно создано"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editCategory(UserDto user, UUID categoryId, CategoryRequest createCategoryRequest)
    throws BadRequestException, NotFoundException, ForbiddenException{
        UUID parentId = createCategoryRequest.getParentId();

        if (parentId != null && parentId.equals(categoryId)) {
            throw new BadRequestException("Категория не может быть родителем для самой себя");
        }
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null) {
            throw new NotFoundException(String.format("Категории с id=%s не существует", categoryId));
        }

        if (parentId != null) {
            ForumCategory parent = categoryRepository.findForumCategoryById(parentId);

            if (parent == null) {
                throw new NotFoundException(String.format("Категория-родитель с id=%s не существует", parentId));
            } else {
                if (!parent.getThemes().isEmpty()){
                   throw new BadRequestException("У данной категории уже присутствуют топики");
                }
                UUID parentOfParentId = parent.getParentId();
                if (parentOfParentId != null && parentOfParentId.equals(categoryId)) {
                    throw new BadRequestException("Категории не могут одновременно быть родителями друг друга");
                }

                parent.getChildCategories().add(forumCategory);
                categoryRepository.saveAndFlush(parent);
            }
        }

        ForumCategory checkCategory = categoryRepository.findByCategoryName(createCategoryRequest.getCategoryName());

        if (checkCategory != null && !Objects.equals(forumCategory.getCategoryName(), checkCategory.getCategoryName())) {
            throw new BadRequestException(String.format("Категория с указанным названием уже существует"));
        }

        forumCategory.setParentId(parentId);
        forumCategory.setCategoryName(createCategoryRequest.getCategoryName());
        forumCategory.setModifiedTime(LocalDateTime.now());

        categoryRepository.saveAndFlush(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно изменена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editTheme(UserDto user, UUID themeId, ThemeRequest createThemeRequest)
    throws BadRequestException, NotFoundException, ForbiddenException {
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }
        else if (forumTheme.getIsArchived()){
            throw new BadRequestException("Тема находится в архиве");
        }

        if (user.getRole() != Role.ADMIN) {
            if  (!Objects.equals(user.getLogin(), forumTheme.getAuthorLogin()) ||
                    user.getRole() != Role.MODERATOR || user.getManageCategoryId() != forumTheme.getCategoryId()) {
                throw new ForbiddenException();
            }
        }

        UUID categoryId = createThemeRequest.getCategoryId();
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null) {
            throw new NotFoundException(String.format("Категория-родитель с id=%s не существует", categoryId));
        }
        else if (!forumCategory.getChildCategories().isEmpty()){
            throw new BadRequestException("Вы не можете создать топик не в категории нижнего уровня");
        }

        ForumTheme checkTheme = themeRepository.findByThemeNameAndCategoryId(createThemeRequest.getThemeName(), createThemeRequest.getCategoryId());

        if (checkTheme != null && !Objects.equals(forumTheme.getThemeName(), checkTheme.getThemeName())
                && checkTheme.getCategoryId() != forumTheme.getCategoryId()) {
            throw new BadRequestException("Тема с указанным названием в данной категории уже существует");
        }

        forumTheme.setThemeName(createThemeRequest.getThemeName());
        forumTheme.setCategoryId(createThemeRequest.getCategoryId());
        forumTheme.setModifiedTime(LocalDateTime.now());

        themeRepository.saveAndFlush(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно изменена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> editMessage(UserDto user, UUID messageId, EditMessageRequest editMessageRequest)
    throws NotFoundException, ForbiddenException{
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        if (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin())) {
            throw new ForbiddenException();
        }
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId());

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
        }

        forumMessage.setModifiedTime(LocalDateTime.now());
        forumMessage.setContent(editMessageRequest.getContent());

        messageRepository.saveAndFlush(forumMessage);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Сообщение успешно изменено"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(UserDto user, UUID categoryId)
            throws NotFoundException, ForbiddenException{
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null) {
            throw new NotFoundException(String.format("Категории с id=%s не существует",categoryId));
        }

        categoryRepository.delete(forumCategory);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Категория успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteTheme(UserDto user, UUID themeId)
            throws NotFoundException, ForbiddenException{
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }

        if (user.getRole() != Role.ADMIN) {
            if  (user.getRole() != Role.MODERATOR || user.getManageCategoryId() != forumTheme.getCategoryId()) {
                throw new ForbiddenException();
            }
        }

        themeRepository.delete(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно удалена"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteMessage(UserDto user, UUID messageId)
            throws NotFoundException, ForbiddenException{
        ForumMessage forumMessage = messageRepository.findForumMessageById(messageId);

        if (forumMessage == null) {
            throw new NotFoundException(String.format("Сообщения с id=%s не существует", messageId));
        }

        if (user.getRole() != Role.ADMIN) {
            if  (!Objects.equals(user.getLogin(), forumMessage.getAuthorLogin()) ||
                    user.getRole() != Role.MODERATOR || user.getManageCategoryId() != forumMessage.getCategoryId()) {
                throw new ForbiddenException();
            }
        }
        ForumTheme forumTheme = themeRepository.findForumThemeById(forumMessage.getThemeId());

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Категория находится в архиве");
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

    public ResponseEntity<?> getMessages(UUID themeId, Integer page, Integer size, SortOrder sortOrder)
    throws NotFoundException{
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
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

    public ResponseEntity<?> checkTheme(UUID themeId)
    throws NotFoundException{
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null){
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> checkCategory(UUID categoryId)
    throws NotFoundException{
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId);

        if (forumCategory == null){
            throw new NotFoundException(String.format("Категории с id=%s не существует", categoryId));
        }

        return ResponseEntity.ok().build();
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

    public ResponseEntity<?> getThemesById(List<UUID> themesId){
        List<ThemeDto> themes = themeRepository.findAllByIdIn(themesId)
                .stream()
                .map(ForumMapper::forumThemeToThemeDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(themes);
    }

    public ResponseEntity<?> archiveTheme(UserDto userDto, UUID themeId){
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }
        else if (forumTheme.getIsArchived()){
            throw new BadRequestException("Тема уже заархивирована");
        }

        if (userDto.getRole() != Role.ADMIN) {
            if (userDto.getManageCategoryId() != forumTheme.getCategoryId() || userDto.getRole() != Role.MODERATOR){
                throw new ForbiddenException();
            }
        }

        forumTheme.setIsArchived(true);
        themeRepository.save(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно заархивирована"), HttpStatus.OK);
    }

    public ResponseEntity<?> unArchiveTheme(UserDto userDto, UUID themeId){
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null) {
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }
        else if (!forumTheme.getIsArchived()){
            throw new BadRequestException("Тема и так не находится в архиве");
        }

        if (userDto.getRole() != Role.ADMIN) {
            if (userDto.getManageCategoryId() != forumTheme.getCategoryId() || userDto.getRole() != Role.MODERATOR){
                throw new ForbiddenException();
            }
        }

        forumTheme.setIsArchived(false);
        themeRepository.save(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно разархивирована"), HttpStatus.OK);
    }

    private Sort.Order getComparator(SortOrder sortOrder) {
        return switch (sortOrder) {
            case CreateDesc -> Sort.Order.desc("createTime");
            case CreateAsc -> Sort.Order.asc("createTime");
            case NameAsc -> Sort.Order.asc("categoryName");
            case NameDesc -> Sort.Order.desc("categoryName");
        };
    }
}
