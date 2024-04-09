package com.hits.user.Rest.Controllers.Auth;

import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.Auth.DTO.UserRegisterModel;
import com.hits.user.Core.Auth.Service.AuthService;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import com.hits.user.Core.RefreshToken.Service.RefreshTokenService;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Контроллер, отвечающий за авторизацию и аутенфикацию")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(REGISTER_USER)
    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет пользователю зарегистрироваться"
    )
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterModel userRegisterModel) throws MessagingException,
            UnsupportedEncodingException,
            UserAlreadyExistsException {
        userRegisterModel.setPassword(passwordEncoder.encode(userRegisterModel.getPassword()));
        userRegisterModel.setLogin(userRegisterModel.getLogin().toLowerCase());

        return authService.registerNewUser(userRegisterModel);
    }

    @PostMapping(LOGIN_USER)
    @Operation(
            summary = "Авторизация пользователя",
            description = "Позволяет пользователю авторизоваться"
    )
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginCredentials loginCredentials) throws AccountNotConfirmedException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getLogin(), loginCredentials.getPassword()));

        if (authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.checkRefreshToken(loginCredentials);

            return authService.loginUser(loginCredentials, refreshToken);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(LOGOUT_USER)
    @Operation(
            summary = "Выход из аккаунта",
            description = "Позволяет пользователю выйти из аккаунта"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String token){
        return authService.logoutUser(token);
    }
}
