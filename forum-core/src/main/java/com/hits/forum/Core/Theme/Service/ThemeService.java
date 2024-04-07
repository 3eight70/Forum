package com.hits.forum.Core.Theme.Service;

import com.hits.common.Core.Theme.DTO.ThemeDto;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.ForbiddenException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Exceptions.ObjectAlreadyExistsException;
import com.hits.forum.Core.Enums.SortOrder;
import com.hits.forum.Core.Theme.DTO.ThemeRequest;
import com.hits.forum.Core.Theme.DTO.ThemeResponse;
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
    ResponseEntity<ThemeResponse> getAllThemes(Integer page, Integer size, SortOrder sortOrder);
    ResponseEntity<List<ThemeDto>> getThemesWithSubstring(String substring);
    ResponseEntity<?> archiveTheme(UserDto user, UUID themeId) throws NotFoundException, ForbiddenException;
    ResponseEntity<?> unArchiveTheme(UserDto user, UUID themeId) throws NotFoundException, ForbiddenException;
    ResponseEntity<?> checkTheme(UUID themeId) throws NotFoundException;
    ResponseEntity<List<ThemeDto>> getThemesById(List<UUID> themesId);
}
