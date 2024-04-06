package com.hits.user.Core.User.Mapper;

import com.hits.common.Models.User.Role;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Core.Admin.DTO.CreateUserModel;
import com.hits.user.Core.User.DTO.UserModel;
import com.hits.user.Core.Auth.DTO.UserRegisterModel;
import com.hits.user.Core.User.Entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserMapper {
    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        return new User(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getEmail(),
                userRegisterModel.getLogin(),
                userRegisterModel.getPhoneNumber(),
                userRegisterModel.getPassword(),
                null,
                false,
                false,
                Role.USER,
                new ArrayList<>(),
                null
        );
    }

    public static User createUserModelToUser(CreateUserModel createUserModel){
        return new User(
                UUID.randomUUID(),
                LocalDateTime.now(),
                createUserModel.getEmail(),
                createUserModel.getLogin(),
                createUserModel .getPhoneNumber(),
                createUserModel.getPassword(),
                null,
                true,
                false,
                createUserModel.getRole() == null ? Role.USER : createUserModel.getRole(),
                new ArrayList<>(),
                null
        );
    }

    public static UserModel userDtoToUserModel(UserDto userDto){
        return new UserModel(
                userDto.getId(),
                userDto.getCreateTime(),
                userDto.getEmail(),
                userDto.getLogin(),
                userDto.getIsConfirmed(),
                userDto.getRole(),
                userDto.getManageCategoryId()
        );
    }

    public static UserDto userToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getCreateTime(),
                user.getEmail(),
                user.getLogin(),
                user.getIsConfirmed(),
                user.getIsBanned(),
                user.getRole(),
                user.getManageCategoryId(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
