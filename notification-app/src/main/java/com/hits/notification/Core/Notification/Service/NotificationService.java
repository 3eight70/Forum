package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.notification.Core.Notification.DTO.NotificationForUserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface NotificationService {
    ResponseEntity<Integer> getAmountOfUnreadNotifications(UserDto userDto);
    ResponseEntity<Page<NotificationForUserModel>> getNotifications(Pageable pageable, UserDto userDto, String search);
    ResponseEntity<Response> readNotification(UserDto userDto, UUID notificationId) throws NotFoundException;
    ResponseEntity<Response> readAllNotifications(UserDto userDto);
}
