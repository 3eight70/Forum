package com.hits.user.Mappers;

import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.common.Entities.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMapper {
    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        return new User(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getEmail(),
                userRegisterModel.getLogin(),
                userRegisterModel.getPassword()
        );
    }
}
