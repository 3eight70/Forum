package com.hits.user.Mappers;

import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
<<<<<<< HEAD
import com.hits.user.Models.Entity.User;
=======
import com.hits.user.Models.Entities.User;
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d

import java.time.LocalDateTime;
import java.util.UUID;

public class UserMapper {
    public static User userRegisterModelToUser(UserRegisterModel userRegisterModel){
        return new User(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userRegisterModel.getEmail(),
<<<<<<< HEAD
=======
                userRegisterModel.getLogin(),
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
                userRegisterModel.getPassword()
        );
    }
}
