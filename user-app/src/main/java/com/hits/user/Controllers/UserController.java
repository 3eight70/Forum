package com.hits.user.Controllers;

import com.hits.common.Client.UserAppClient;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.RefreshToken;
import com.hits.user.Services.IRefreshTokenService;
import com.hits.user.Services.IUserService;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterModel userRegisterModel){
        userRegisterModel.setPassword(passwordEncoder.encode(userRegisterModel.getPassword()));

        try{
            return userService.registerNewUser(userRegisterModel);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Данные введены некорректно"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(LOGIN_USER)
    public ResponseEntity<?> loginUser(@RequestBody LoginCredentials loginCredentials){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(), loginCredentials.getPassword()));

            if (authentication.isAuthenticated()){
                RefreshToken refreshToken = refreshTokenService.checkRefreshToken(loginCredentials);

                return userService.loginUser(loginCredentials, refreshToken);
            }
        }
        catch (BadCredentialsException | UsernameNotFoundException e) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Действие токена истекло"), HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(LOGOUT_USER)
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token){
        try {
            return userService.logoutUser(token);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean validateToken(@RequestParam(name = "token") String token){
            return userService.validateToken(token);
    }

    @Override
    public UserDto getUser(@RequestParam(name = "login") String login){
        return userService.getUserFromLogin(login);
    }

    @PostMapping(ADD_TO_FAVORITE)
    public ResponseEntity<?> addThemeToFavorite(@AuthenticationPrincipal UserDto userDto, @RequestParam(name = "themeId") UUID themeId){
        try {
            return userService.addThemeToFavorite(userDto, themeId);
        }
        catch (FeignException.BadRequest e){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Темы с указанным id не существует"), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_FROM_FAVORITE)
    public ResponseEntity<?> deleteThemeFromFavorite(@AuthenticationPrincipal UserDto userDto, @RequestParam(name = "themeId") UUID themeId){
        try {
            return userService.deleteThemeFromFavorite(userDto, themeId);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(GET_FAVORITE)
    public ResponseEntity<?> getFavoriteThemes(@AuthenticationPrincipal UserDto userDto){
        try {
            return userService.getFavoriteThemes(userDto);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(VERIFY_USER)
    public ResponseEntity<?> verifyUser(
            @RequestParam(name = "id") UUID userId,
            @RequestParam(name = "code") String code){
        try {
            return userService.verifyUser(userId, code);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(BAN_USER)
    public ResponseEntity<?> banUser(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId){
        try {
            return userService.banUser(user, userId);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(GIVE_MODERATOR)
    public ResponseEntity<?> giveModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId){
        try {
            return userService.giveModeratorRole(user, userId);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(DELETE_MODERATOR)
    public ResponseEntity<?> deleteModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId){
        try {
            return userService.deleteModeratorRole(user, userId);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
