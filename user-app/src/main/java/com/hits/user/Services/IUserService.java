package com.hits.user.Services;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import com.hits.user.Models.Dto.UserDto.CreateUserModel;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserEditModel;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Models.Entities.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public interface IUserService {
    User loadUserByUsername(String email) throws UsernameNotFoundException;

    ResponseEntity<?> registerNewUser(UserRegisterModel userRegisterModel) throws MessagingException,
            UnsupportedEncodingException,
            UserAlreadyExistsException;

    ResponseEntity<?> loginUser(LoginCredentials loginCredentials, RefreshToken refreshToken) throws AccountNotConfirmedException;

    Boolean validateToken(String token);

    ResponseEntity<?> logoutUser(String token);

    UserDto getUserFromLogin(String login) throws NotFoundException;

    ResponseEntity<?> addThemeToFavorite(UserDto userDto, UUID themeId) throws NotFoundException;

    ResponseEntity<?> deleteThemeFromFavorite(UserDto userDto, UUID themeId) throws NotFoundException;

    ResponseEntity<?> getFavoriteThemes(UserDto userDto);

    ResponseEntity<?> verifyUser(UUID userId, String code) throws NotFoundException, BadRequestException;

    ResponseEntity<?> banUser(UserDto user, UUID userId) throws NotFoundException, BadRequestException;

    ResponseEntity<?> giveModeratorRole(UserDto user, UUID userId) throws NotFoundException;

    ResponseEntity<?> deleteModeratorRole(UserDto user, UUID userId) throws NotFoundException;

    ResponseEntity<?> giveCategoryToModerator(UserDto user, UUID userId, UUID categoryId) throws NotFoundException, BadRequestException;

    ResponseEntity<?> createUser(CreateUserModel createUserModel);

    ResponseEntity<?> editUser(UserEditModel userEditModel, UUID userId);
}