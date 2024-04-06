package com.hits.user.Core.User.Service;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Core.User.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface UserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;

    UserDto getUserFromLogin(String login) throws NotFoundException;

    ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId) throws NotFoundException;

    ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId) throws NotFoundException;

    ResponseEntity<?> getFavoriteThemes(UserDto userDto);

    ResponseEntity<?> verifyUser(UUID userId, String code) throws NotFoundException, BadRequestException;
    Boolean validateToken(String token);
}