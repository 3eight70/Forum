package com.hits.user.Controllers;

import com.hits.common.Client.UserAppClient;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Services.IRefreshTokenService;
import com.hits.user.Services.IUserService;
import feign.FeignException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAppClient {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(REGISTER_USER)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterModel userRegisterModel) throws MessagingException,
            UnsupportedEncodingException,
            UserAlreadyExistsException  {
        userRegisterModel.setPassword(passwordEncoder.encode(userRegisterModel.getPassword())) ;
        return userService.registerNewUser(userRegisterModel);
    }

    @PostMapping(LOGIN_USER)
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginCredentials loginCredentials) throws AccountNotConfirmedException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(), loginCredentials.getPassword()));

        if (authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.checkRefreshToken(loginCredentials);

            return userService.loginUser(loginCredentials, refreshToken);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(LOGOUT_USER)
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token){
        return userService.logoutUser(token);
    }

    @Override
    public Boolean validateToken(@RequestParam(name = "token") String token) {
        return userService.validateToken(token);
    }

    @Override
    public UserDto getUser(@RequestParam(name = "login") String login) throws NotFoundException{
        return userService.getUserFromLogin(login);
    }

    @PostMapping(ADD_TO_FAVORITE)
    public ResponseEntity<?> addThemeToFavorite(@AuthenticationPrincipal UserDto userDto,
                                                @RequestParam(name = "themeId") UUID themeId) throws NotFoundException{
        try {
            return userService.addThemeToFavorite(userDto, themeId);
        }
        catch (FeignException.BadRequest e){
            return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND.value(), "Темы с указанным id не существует"), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(DELETE_FROM_FAVORITE)
    public ResponseEntity<?> deleteThemeFromFavorite(@AuthenticationPrincipal UserDto userDto,
                                                     @RequestParam(name = "themeId") UUID themeId) throws NotFoundException{
        return userService.deleteThemeFromFavorite(userDto, themeId);
    }

    @GetMapping(GET_FAVORITE)
    public ResponseEntity<?> getFavoriteThemes(@AuthenticationPrincipal UserDto userDto){
        return userService.getFavoriteThemes(userDto);
    }

    @GetMapping(VERIFY_USER)
    public ResponseEntity<?> verifyUser(
            @RequestParam(name = "id") UUID userId,
            @RequestParam(name = "code") String code) throws NotFoundException, BadRequestException {
        return userService.verifyUser(userId, code);
    }

    @PostMapping(BAN_USER)
    public ResponseEntity<?> banUser(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId) throws NotFoundException, BadRequestException {
        return userService.banUser(user, userId);
    }

    @PostMapping(GIVE_MODERATOR)
    public ResponseEntity<?> giveModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId) throws NotFoundException {

        return userService.giveModeratorRole(user, userId);
    }

    @DeleteMapping(DELETE_MODERATOR)
    public ResponseEntity<?> deleteModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId) throws NotFoundException {
        return userService.deleteModeratorRole(user, userId);
    }

    @PostMapping(GIVE_CATEGORY)
    public ResponseEntity<?> giveCategoryToModerator(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "categoryId") UUID categoryId) throws NotFoundException, BadRequestException {
        return userService.giveCategoryToModerator(user, userId, categoryId);
    }
}
