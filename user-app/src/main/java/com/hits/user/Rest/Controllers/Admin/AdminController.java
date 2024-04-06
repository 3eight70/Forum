package com.hits.user.Rest.Controllers.Admin;

import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Core.Admin.DTO.CreateUserModel;
import com.hits.user.Core.Admin.DTO.UserEditModel;
import com.hits.user.Core.Admin.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.hits.common.Consts.*;
import static com.hits.common.Consts.EDIT_USER;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(BAN_USER)
    public ResponseEntity<?> banUser(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId) throws NotFoundException, BadRequestException {
        return adminService.banUser(user, userId);
    }

    @PostMapping(GIVE_MODERATOR)
    public ResponseEntity<?> giveModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId) throws NotFoundException {

        return adminService.giveModeratorRole(user, userId);
    }

    @DeleteMapping(DELETE_MODERATOR)
    public ResponseEntity<?> deleteModeratorRole(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId) throws NotFoundException {
        return adminService.deleteModeratorRole(user, userId);
    }

    @PostMapping(GIVE_CATEGORY)
    public ResponseEntity<?> giveCategoryToModerator(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "categoryId") UUID categoryId) throws NotFoundException, BadRequestException {
        return adminService.giveCategoryToModerator(user, userId, categoryId);
    }

    @PostMapping(CREATE_USER)
    public ResponseEntity<?> createUser(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody CreateUserModel createUserModel
    ) {
        createUserModel.setPassword(passwordEncoder.encode(createUserModel.getPassword()));
        return adminService.createUser(createUserModel);
    }

    @PutMapping(EDIT_USER)
    public ResponseEntity<?> editUser(
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody UserEditModel userEditModel,
            @RequestParam(name = "userId") UUID userId
    ) {
        userEditModel.setPassword(passwordEncoder.encode(userEditModel.getPassword()));
        return adminService.editUser(userEditModel, userId);
    }
}
