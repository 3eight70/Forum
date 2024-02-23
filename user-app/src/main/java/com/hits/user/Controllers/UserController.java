package com.hits.user.Controllers;

import com.hits.user.Models.Dto.Response.Response;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entity.RefreshToken;
import com.hits.user.Services.IRefreshTokenService;
import com.hits.user.Services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/account/register")
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

    @PostMapping("/account/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginCredentials loginCredentials){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(), loginCredentials.getPassword()));

            if (authentication.isAuthenticated()){
                RefreshToken refreshToken = refreshTokenService.checkRefreshToken(loginCredentials);

                return userService.loginUser(loginCredentials, refreshToken);
            }
        }
        catch (BadCredentialsException  e) {
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }
        catch (UsernameNotFoundException e){
            return new ResponseEntity<>(new Response(HttpStatus.BAD_REQUEST.value(), "Неправильный логин или пароль"), HttpStatus.BAD_REQUEST);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Действие токена истекло"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
