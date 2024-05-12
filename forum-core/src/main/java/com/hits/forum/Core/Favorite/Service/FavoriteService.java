package com.hits.forum.Core.Favorite.Service;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface FavoriteService {
    ResponseEntity<Page<ThemeDto>> getFavoriteThemes(UserDto userDto, Pageable pageable);
    ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId) throws NotFoundException;
    ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId) throws NotFoundException;
}
