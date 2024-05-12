package com.hits.forum.Core.Theme.Service;

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
import com.hits.forum.Core.Theme.DTO.ThemeRequest;
import com.hits.forum.Core.Theme.Entity.ForumTheme;
import com.hits.forum.Core.Theme.Mapper.ThemeMapper;
import com.hits.forum.Core.Theme.Repository.ThemeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public ResponseEntity<?> createTheme(UserDto user, ThemeRequest createThemeRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException {
        String themeName = createThemeRequest.getThemeName();
        UUID categoryId = createThemeRequest.getCategoryId();
        themeRepository.findByThemeNameAndCategoryId(themeName, categoryId)
                .ifPresent(theme -> {
                    throw new ObjectAlreadyExistsException(String.format("В данной категории уже существует тема с названием=%s", themeName));
                });
        ForumTheme forumTheme;

        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория-родитель с id=%s не существует", categoryId)));

        if (!forumCategory.getChildCategories().isEmpty()){
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
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Тема находится в архиве");
        }

        checkAccess(user, forumTheme);

        UUID categoryId = createThemeRequest.getCategoryId();
        ForumCategory forumCategory = categoryRepository.findForumCategoryById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория-родитель с id=%s не существует", categoryId)));

        if (!forumCategory.getChildCategories().isEmpty()){
            throw new BadRequestException("Вы не можете создать топик не в категории нижнего уровня");
        }

        themeRepository.findByThemeNameAndCategoryId(createThemeRequest.getThemeName(), createThemeRequest.getCategoryId())
                .ifPresent(theme -> {
                    if (!Objects.equals(forumTheme.getThemeName(), theme.getThemeName())
                            && theme.getCategoryId() != forumTheme.getCategoryId()) {
                        throw new BadRequestException("Тема с указанным названием в данной категории уже существует");
                    }
                });

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
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        checkAccess(user, forumTheme);

        themeRepository.delete(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно удалена"), HttpStatus.OK);
    }

    public ResponseEntity<Page<ThemeDto>> getAllThemes(Pageable pageable){
        return ResponseEntity.ok(new PageImpl<>(themeRepository.findAll(pageable)
                .stream()
                .map(ThemeMapper::forumThemeToThemeDto)
                .collect(Collectors.toList())));
    }

    public ResponseEntity<?> checkTheme(UUID themeId)
            throws NotFoundException{
        themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<ThemeDto>> getThemesWithSubstring(String substring){
        List<ThemeDto> forumThemes = themeRepository.findAllByThemeNameContainingIgnoreCase(substring)
                .stream()
                .map(ThemeMapper::forumThemeToThemeDto)
                .toList();

        return ResponseEntity.ok(forumThemes);
    }

    @Transactional
    public ResponseEntity<?> archiveTheme(UserDto userDto, UUID themeId){
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        if (forumTheme.getIsArchived()){
            throw new BadRequestException("Тема уже заархивирована");
        }

        checkAccess(userDto, forumTheme);

        forumTheme.setIsArchived(true);
        themeRepository.save(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно заархивирована"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> unArchiveTheme(UserDto userDto, UUID themeId){
        ForumTheme forumTheme = themeRepository.findForumThemeById(themeId)
                .orElseThrow(() -> new NotFoundException(String.format("Темы с id=%s не существует", themeId)));

        if (!forumTheme.getIsArchived()){
            throw new BadRequestException("Тема и так не находится в архиве");
        }

        if (userDto.getRole() != Role.ADMIN) {
            if (!userDto.getManageCategoryId().contains(forumTheme.getCategoryId()) || userDto.getRole() != Role.MODERATOR){
                throw new ForbiddenException();
            }
        }

        forumTheme.setIsArchived(false);
        themeRepository.save(forumTheme);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Тема успешно разархивирована"), HttpStatus.OK);
    }

    private void checkAccess(UserDto user, ForumTheme forumTheme){
        if (user.getRole() != Role.ADMIN) {
            if  (!Objects.equals(user.getLogin(), forumTheme.getAuthorLogin()) &&
                    (user.getRole() != Role.MODERATOR || !user.getManageCategoryId().contains(forumTheme.getCategoryId()))) {
                throw new ForbiddenException();
            }
        }
    }
}
