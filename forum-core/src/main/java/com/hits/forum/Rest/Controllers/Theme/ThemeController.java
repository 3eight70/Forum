package com.hits.forum.Rest.Controllers.Theme;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Enums.SortOrder;
import com.hits.forum.Core.Theme.DTO.ThemeRequest;
import com.hits.forum.Core.Theme.DTO.ThemeResponse;
import com.hits.forum.Core.Theme.Service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Темы", description = "Отвечает за работу с темами")
public class ThemeController {
    private final ThemeService themeService;

    @PostMapping(CREATE_THEME)
    @Operation(
            summary = "Создание темы",
            description = "Позволяет создать тему в категории"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createTheme(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody ThemeRequest createThemeRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException {
        return themeService.createTheme(user, createThemeRequest);
    }

    @PutMapping(EDIT_THEME)
    @Operation(
            summary = "Изменение темы",
            description = "Позволяет изменить тему"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> editTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "themeId") @Parameter(description = "Идентификатор темы") UUID themeId,
            @Valid @RequestBody ThemeRequest createThemeRequest)
            throws BadRequestException, NotFoundException, ForbiddenException
    {
        return themeService.editTheme(user, themeId, createThemeRequest);
    }

    @DeleteMapping(DELETE_THEME)
    @Operation(
            summary = "Удаление темы",
            description = "Позволяет удалить тему"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "themeId") @Parameter(description = "Идентификатор темы") UUID themeId)
            throws NotFoundException, ForbiddenException{
        return themeService.deleteTheme(user, themeId);
    }

    @GetMapping(GET_THEMES)
    @Operation(
            summary = "Получение списка тем",
            description = "Позволяет получить список тем, с использованием пагинации и сортировки"
    )
    public ResponseEntity<ThemeResponse> getThemes(
            @RequestParam(name = "page", required = false, defaultValue = "0") @Parameter(description = "Номер страницы") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") @Parameter(description = "Количество элементов на странице") Integer size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "CreateDesc") @Parameter(description = "Вариант сортировки") SortOrder sortOrder){
        return themeService.getAllThemes(page, size, sortOrder);
    }

    @GetMapping(GET_THEMES_WITH_SUBSTRING)
    @Operation(
            summary = "Поиск тем по подстроке",
            description = "Позволяет получить список тем, используя поиск по подстроке"
    )
    public ResponseEntity<List<ThemeDto>> getThemesWithSubstring(@RequestParam(value = "name") @Parameter(description = "Название темы") String name){
        return themeService.getThemesWithSubstring(name);
    }

    @PostMapping(ARCHIVE_THEME)
    @Operation(
            summary = "Архивация темы",
            description = "Позволяет добавить тему в архив"
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<?> archiveTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(value = "themeId") @Parameter(description = "Идентификатор темы") UUID themeId)
            throws NotFoundException, ForbiddenException {
        return themeService.archiveTheme(user, themeId);
    }

    @DeleteMapping(ARCHIVE_THEME)
    @Operation(
            summary = "Разархивация темы",
            description = "Позволяет вытащить тему из архива"
    )
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<?> unArchiveTheme(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(value = "themeId") @Parameter(description = "Идентификатор темы") UUID themeId)
            throws NotFoundException, ForbiddenException {
        return themeService.unArchiveTheme(user, themeId);
    }
}
