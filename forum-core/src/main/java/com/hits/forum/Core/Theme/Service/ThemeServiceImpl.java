package com.hits.forum.Core.Theme.Service;

import com.hits.common.Core.Page.DTO.PageResponse;
import com.hits.common.Core.Response.Response;
import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.Role;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Category.Entity.ForumCategory;
import com.hits.forum.Core.Category.Repository.CategoryRepository;
import com.hits.forum.Core.Enums.SortOrder;
import com.hits.forum.Core.Theme.DTO.ThemeRequest;
import com.hits.forum.Core.Theme.DTO.ThemeResponse;
import com.hits.forum.Core.Theme.Entity.ForumTheme;
import com.hits.forum.Core.Theme.Mapper.ThemeMapper;
import com.hits.forum.Core.Theme.Repository.ThemeRepository;
import com.hits.forum.Core.Utils.ComparatorProvider;
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
public class ThemeServiceImpl implements ThemeService{
    private final CategoryRepository categoryRepository;
    private final ThemeRepository themeRepository;

    @Value("${jwt.secret}")
    private String secret;

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

        forumTheme = ThemeMapper.themeRequestToForumTheme(user.getLogin(), createThemeRequest);

        themeRepository.saveAndFlush(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно создана"), HttpStatus.OK);
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

    public ResponseEntity<ThemeResponse> getAllThemes(Integer page, Integer size, SortOrder sortOrder){
        Sort sort = Sort.by(ComparatorProvider.getComparator(sortOrder));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ForumTheme> themesPage = themeRepository.findAll(pageable);

        List<ThemeDto> themeRequests = themesPage.getContent().stream()
                .map(ThemeMapper::forumThemeToThemeDto)
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

    public ResponseEntity<?> checkTheme(UUID themeId)
            throws NotFoundException{
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId);

        if (forumTheme == null){
            throw new NotFoundException(String.format("Темы с id=%s не существует", themeId));
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<ThemeDto>> getThemesWithSubstring(String substring){
        List<ThemeDto> forumThemes = themeRepository.findAllByThemeNameContainingIgnoreCase(substring)
                .stream()
                .map(ThemeMapper::forumThemeToThemeDto)
                .toList();

        return ResponseEntity.ok(forumThemes);
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

    public ResponseEntity<List<ThemeDto>> getThemesById(List<UUID> themesId){
        List<ThemeDto> themes = themeRepository.findAllByIdIn(themesId)
                .stream()
                .map(ThemeMapper::forumThemeToThemeDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(themes);
    }
}
