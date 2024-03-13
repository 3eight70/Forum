package com.hits.user.Mappers;

import com.hits.common.Models.User.UserDto;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public static UserDto userToUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getCreateTime(),
                user.getEmail(),
                user.getLogin(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
    }
}
