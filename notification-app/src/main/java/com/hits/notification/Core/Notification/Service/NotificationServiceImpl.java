package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.notification.Core.Notification.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    public ResponseEntity<Integer> getAmountOfUnreadNotifications(UserDto userDto){
        return ResponseEntity.ok(notificationRepository.countByIsReadFalse(userDto.getLogin()));
    }
}
