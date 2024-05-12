package com.hits.common.Core.User;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Core.User.DTO.UserNotificationDTO;

public final class UserNotificationMapper {
    public static UserNotificationDTO userDtoToUserNotificationDto(UserDto userDto){
        return new UserNotificationDTO(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getLogin()
        );
    }
}
