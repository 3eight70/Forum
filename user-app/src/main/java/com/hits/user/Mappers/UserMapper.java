package com.hits.user.Mappers;

import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class UserMapper {
    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        return new User(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getEmail(),
                userRegisterModel.getPassword()
        );
    }
}
