package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.User.DTO.UserDto;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<Integer> getAmountOfUnreadNotifications(UserDto userDto);
}
