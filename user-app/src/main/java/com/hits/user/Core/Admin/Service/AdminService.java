package com.hits.user.Core.Admin.Service;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.BadRequestException;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.user.Core.Admin.DTO.CreateUserModel;
import com.hits.user.Core.Admin.DTO.UserEditModel;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AdminService {
    ResponseEntity<?> banUser(UserDto user, UUID userId) throws NotFoundException, BadRequestException;

    ResponseEntity<?> giveModeratorRole(UserDto user, UUID userId) throws NotFoundException;

    ResponseEntity<?> deleteModeratorRole(UserDto user, UUID userId) throws NotFoundException;

    ResponseEntity<?> giveCategoryToModerator(UserDto user, UUID userId, UUID categoryId) throws NotFoundException, BadRequestException;

    ResponseEntity<?> createUser(CreateUserModel createUserModel);

    ResponseEntity<?> editUser(UserEditModel userEditModel, UUID userId);
}
