package com.hits.user.Rest.Controllers.User;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.Response.Response;
import com.hits.common.Models.User.UserDto;
import com.hits.security.Rest.Client.UserAppClient;
import com.hits.user.Exceptions.AccountNotConfirmedException;
import com.hits.user.Exceptions.UserAlreadyExistsException;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.Auth.DTO.LoginCredentials;
import com.hits.user.Core.Auth.DTO.UserRegisterModel;
import com.hits.user.Core.RefreshToken.Entity.RefreshToken;
import com.hits.user.Core.RefreshToken.Service.RefreshTokenService;
import com.hits.user.Core.User.Service.UserService;
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
public class    UserController implements UserAppClient {
    private final UserService userService;

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

    @GetMapping(GET_PROFILE)
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDto userDto){
        return ResponseEntity.ok(UserMapper.userDtoToUserModel(userDto));
    }

    @GetMapping(VALIDATE_TOKEN)
    public Boolean validateToken(@RequestParam("token") String token){
        return userService.validateToken(token);
    }
}
