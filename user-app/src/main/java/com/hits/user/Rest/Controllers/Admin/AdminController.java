package com.hits.user.Rest.Controllers.Admin;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.user.Core.Admin.DTO.CreateUserModel;
import com.hits.user.Core.Admin.DTO.UserEditModel;
import com.hits.user.Core.Admin.Service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.hits.common.Core.Consts.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Администратор", description = "Позволяет выполнять действия, доступные только администратору")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(BAN_USER)
    @Operation(
            summary = "Блокировка пользователя",
            description = "Позволяет заблокировать пользователя"
    )
    public ResponseEntity<?> banUser(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") @Parameter(name = "Идентификатор пользователя") UUID userId
    ) throws NotFoundException, BadRequestException {
        return adminService.banUser(user, userId);
    }

    @PostMapping(GIVE_MODERATOR)
    @Operation(
            summary = "Выдача роли модератора",
            description = "Позволяет выдать роль модератора пользователю"
    )
    public ResponseEntity<?> giveModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") @Parameter(name = "Идентификатор пользователя") UUID userId) throws NotFoundException {

        return adminService.giveModeratorRole(user, userId);
    }

    @DeleteMapping(DELETE_MODERATOR)
    @Operation(
            summary = "Удаление роли модератора",
            description = "Позволяет забрать роль модератора у пользователя"
    )
    public ResponseEntity<?> deleteModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") @Parameter(name = "Идентификатор пользователя") UUID userId) throws NotFoundException {
        return adminService.deleteModeratorRole(user, userId);
    }

    @PostMapping(GIVE_CATEGORY)
    @Operation(
            summary = "Назначение модератора на категорию",
            description = "Позволяет назначить модератора на управление категорией"
    )
    public ResponseEntity<?> giveCategoryToModerator(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") @Parameter(name = "Идентификатор пользователя") UUID userId,
            @RequestParam(name = "categoryId") @Parameter(name = "Идентификатор категории") UUID categoryId) throws NotFoundException, BadRequestException {
        return adminService.giveCategoryToModerator(user, userId, categoryId);
    }

    @PostMapping(CREATE_USER)
    @Operation(
            summary = "Создание пользователя",
            description = "Позволяет администратору создать пользователя"
    )
    public ResponseEntity<?> createUser(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody CreateUserModel createUserModel
    ) {
        createUserModel.setPassword(passwordEncoder.encode(createUserModel.getPassword()));
        return adminService.createUser(createUserModel);
    }

    @PutMapping(EDIT_USER)
    @Operation(
            summary = "Изменение пользователя",
            description = "Позволяет администратору изменить пользователя"
    )
    public ResponseEntity<?> editUser(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody UserEditModel userEditModel,
            @RequestParam(name = "userId") @Parameter(name = "Идентификатор пользователя") UUID userId
    ) {
        userEditModel.setPassword(passwordEncoder.encode(userEditModel.getPassword()));
        return adminService.editUser(userEditModel, userId);
    }
}
