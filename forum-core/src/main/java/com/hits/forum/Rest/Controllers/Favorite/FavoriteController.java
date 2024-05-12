package com.hits.forum.Rest.Controllers.Favorite;

import com.hits.common.Core.Response.Response;
import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.forum.Core.Favorite.Service.FavoriteService;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(
            summary = "Добавление темы в избранное",
            description = "Позволяет добавить тему в избранное пользователя"
    )
    @PostMapping(ADD_TO_FAVORITE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addThemeToFavorite(
            @AuthenticationPrincipal UserDto userDto,
            @RequestParam(name = "themeId") @Parameter(description = "Идентификатор темы") UUID themeId
    ) throws NotFoundException {
        try {
            return favoriteService.addThemeToFavorite(userDto, themeId);
        }
        catch (FeignException.BadRequest e){
            return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(), "Темы с указанным id не существует"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Удаление темы из избранного",
            description = "Позволяет удалить тему из избранного"
    )
    @DeleteMapping(DELETE_FROM_FAVORITE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteThemeFromFavorite(
            @AuthenticationPrincipal UserDto userDto,
            @RequestParam(name = "themeId") @Parameter(description = "Идентификатор темы") UUID themeId
    ) throws NotFoundException{
        return favoriteService.deleteThemeFromFavorite(userDto, themeId);
    }

    @Operation(
            summary = "Получение списка избранных тем",
            description = "Позволяет получить список избранных тем"
    )
    @GetMapping(GET_FAVORITE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ThemeDto>> getFavoriteThemes(
            @AuthenticationPrincipal UserDto userDto,
            @ParameterObject @PageableDefault(sort = "addTime", direction = Sort.Direction.DESC) Pageable pageable)
        {
        return favoriteService.getFavoriteThemes(userDto, pageable);
    }
}
