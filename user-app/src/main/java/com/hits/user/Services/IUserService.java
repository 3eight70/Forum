package com.hits.user.Services;

import com.hits.common.Models.User.UserDto;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public interface IUserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;
    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel) throws MessagingException, UnsupportedEncodingException;
    ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken);
    Boolean validateToken(String token);
    ResponseEntity<?> logoutUser(String token);
    UserDto getUserFromLogin(String login);
    ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId);
    ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId);
    ResponseEntity<?> getFavoriteThemes(UserDto userDto);
    ResponseEntity<?> verifyUser(UUID userId, String code);
}
