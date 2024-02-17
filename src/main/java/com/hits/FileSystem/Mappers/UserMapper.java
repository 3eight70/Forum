package com.hits.FileSystem.Mappers;

import com.hits.FileSystem.Models.Dto.UserDto.UserRegisterModel;
import com.hits.FileSystem.Models.Entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class UserMapper {
    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        String email = userRegisterModel.getEmail();
        String modifiedEmail = email.replace("@", "").replace(".", "");

        return new User(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getEmail(),
                modifiedEmail,
                userRegisterModel.getPassword()
        );
    }
}
