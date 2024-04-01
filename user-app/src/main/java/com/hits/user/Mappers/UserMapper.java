package com.hits.user.Mappers;

import com.hits.common.Models.User.Role;
import com.hits.common.Models.User.UserDto;
import com.hits.user.Models.Dto.UserDto.UserRegisterModel;
import com.hits.user.Models.Entities.User;
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
