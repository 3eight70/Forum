package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.Response.Response;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.notification.Core.Notification.DTO.NotificationForUserModel;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Mapper.NotificationMapper;
import com.hits.notification.Core.Notification.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    public ResponseEntity<Integer> getAmountOfUnreadNotifications(UserDto userDto){
        return ResponseEntity.ok(notificationRepository.countByIsReadFalse(userDto.getLogin()));
    }

    public ResponseEntity<Page<NotificationForUserModel>> getNotifications(Pageable pageable, UserDto userDto, String search){
        Page<Notification> page = notificationRepository.findAllByUserLoginOrderByIsReadAsc(pageable, userDto.getLogin());

        List<NotificationForUserModel> mappedNotifications = page.getContent()
                .stream()
                .map(NotificationMapper::notificationToNotificationForUser)
                .toList();

        return ResponseEntity.ok(new PageImpl<>(mappedNotifications, pageable, page.getTotalElements()));
    }

    public ResponseEntity<Response> readNotification(UserDto userDto, UUID notificationId) throws NotFoundException {
        Notification notification = notificationRepository.findByUserLoginAndId(userDto.getLogin(), notificationId)
                .orElseThrow(() -> new NotFoundException("Уведомление с указанным id не найдено"));

        notification.setIsRead(true);
        notificationRepository.save(notification);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Уведомление успешно прочитано"), HttpStatus.OK);
    }

    public ResponseEntity<Response> readAllNotifications(UserDto userDto) {
        List<Notification> notifications = notificationRepository.findAllUnreadNotificationsByUserLogin(userDto.getLogin());

        notifications = notifications
                .stream()
                .peek((notification) -> notification.setIsRead(true))
                .toList();

        notificationRepository.saveAll(notifications);

        return new ResponseEntity<>(new Response(HttpStatus.OK.value(),
                "Все уведомления успешно прочитаны"), HttpStatus.OK);
    }
}
