package com.hits.forum.Core.Theme.Service;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Theme.DTO.ThemeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ThemeService {
    ResponseEntity<?> createTheme(UserDto user, ThemeRequest createThemeRequest)
            throws ObjectAlreadyExistsException, NotFoundException, BadRequestException;
    ResponseEntity<?> editTheme(UserDto user, UUID themeId, ThemeRequest createThemeRequest)
            throws BadRequestException, NotFoundException, ForbiddenException;
    ResponseEntity<?> deleteTheme(UserDto user, UUID themeId)
            throws NotFoundException, ForbiddenException;
    ResponseEntity<Page<ThemeDto>> getAllThemes(Pageable pageable);
    ResponseEntity<List<ThemeDto>> getThemesWithSubstring(String substring);
    ResponseEntity<?> archiveTheme(UserDto user, UUID themeId) throws NotFoundException, ForbiddenException;
    ResponseEntity<?> unArchiveTheme(UserDto user, UUID themeId) throws NotFoundException, ForbiddenException;
    ResponseEntity<?> checkTheme(UUID themeId) throws NotFoundException;
    ResponseEntity<List<ThemeDto>> getFavoriteThemes(UserDto userDto);
    ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId) throws NotFoundException;
    ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId) throws NotFoundException;
}
