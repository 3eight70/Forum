package com.hits.user.Rest.Controllers.User;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.security.Rest.Client.UserAppClient;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Пользователь", description = "Отвечает за работу с пользователем")
public class UserController implements UserAppClient {
    private final UserService userService;

    @Operation(
            summary = "Получение информации пользователя",
            description = "Позволяет проверить, существует ли пользователь с указанным логином и получить информацию о нем")
    @Override
    public UserDto getUser(
            @RequestParam(name = "login") @Parameter(description = "Логин пользователя") String login
    ) throws NotFoundException{
        return userService.getUserFromLogin(login);
    }

    @Operation(
            summary = "Подтверждение аккаунта",
            description = "Позволяет подтвердить аккаунт пользователя после регистрации"
    )
    @GetMapping(VERIFY_USER)
    public ResponseEntity<?> verifyUser(
            @RequestParam(name = "id") @Parameter(description = "Идентификатор пользователя") UUID userId,
            @RequestParam(name = "code") @Parameter(description = "Код подтверждения аккаунта") String code
    ) throws NotFoundException, BadRequestException {
        return userService.verifyUser(userId, code);
    }

    @Operation(
            summary = "Получение профиля",
            description = "Позволяет получить профиль пользователя"
    )
    @GetMapping(GET_PROFILE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDto userDto){
        return ResponseEntity.ok(UserMapper.userDtoToUserModel(userDto));
    }

    @Operation(
            summary = "Валидация access токена",
            description = "Позволяет проверить токен на валидность"
    )
    @GetMapping(VALIDATE_TOKEN)
    public Boolean validateToken(
            @RequestParam("token") @Parameter(description = "access токен") String token
            ){
        return userService.validateToken(token);
    }
}
