package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.User.DTO.UserDto;
import com.hits.notification.Core.Notification.DTO.NotificationForUserModel;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Mapper.NotificationMapper;
import com.hits.notification.Core.Notification.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    public ResponseEntity<Integer> getAmountOfUnreadNotifications(UserDto userDto){
        return ResponseEntity.ok(notificationRepository.countByIsReadFalse(userDto.getLogin()));
    }

    public ResponseEntity<Page<NotificationForUserModel>> getNotifications(Pageable pageable, UserDto userDto, String search){
        Page<Notification> page = notificationRepository.findAllByUserLogin(pageable, userDto.getLogin());
        List<Notification> notifications = page.getContent();

        List<Notification> sortedNotifications = notifications
                .stream()
                .sorted(Comparator.comparing(Notification::getIsRead)
                        .thenComparing(Notification::getCreateTime))
                .toList();

        List<Notification> notificationsToCommit = sortedNotifications
                .stream()
                .peek(notification -> {
                    if (!notification.getIsRead()) {
                        notification.setIsRead(true);
                    }
                })
                .limit(pageable.getPageSize())
                .toList();

        notificationRepository.saveAll(notificationsToCommit);

        return ResponseEntity.ok(new PageImpl<>(sortedNotifications
                .stream()
                .map(NotificationMapper::notificationToNotificationForUser)
                .collect(Collectors.toList()), pageable, page.getTotalElements()));

    }
}
