package com.hits.user.Controllers;

<<<<<<< HEAD
import com.hits.user.Models.Dto.Response.Response;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entity.RefreshToken;
=======
import com.hits.common.Models.Response.Response;
import com.hits.user.Models.Dto.UserDto.LoginCredentials;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
import com.hits.user.Services.IRefreshTokenService;
import com.hits.user.Services.IUserService;
import jakarta.validation.Valid;
import com.hits.user.Models.Entities.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.hits.common.Consts.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

<<<<<<< HEAD
    public static final String REGISTER_USER = "/api/account/register";
    public static final String LOGIN_USER = "/api/account/login";

=======
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
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

    @GetMapping(VALIDATE_TOKEN)
    public ResponseEntity<?> validateToken(@RequestParam(name = "token") String token){
        try{
            Boolean valid = userService.validateToken(token);

            if (valid){
                return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "Токен валиден"), HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(new Response(HttpStatus.UNAUTHORIZED.value(), "Токен не валиден"), HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e){
            return new ResponseEntity<>(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
