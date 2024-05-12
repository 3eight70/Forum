package com.hits.notification.Core.Notification.Mapper;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import com.hits.notification.Core.Notification.DTO.NotificationForUserModel;
import com.hits.notification.Core.Notification.Entity.Notification;

import java.util.UUID;

public final class NotificationMapper {
    public static NotificationForUserModel notificationToNotificationForUser(Notification notification){
        return new NotificationForUserModel(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getCreateTime(),
                notification.getIsRead()
        );
    }

    public static Notification notificationDtoToNotification(NotificationDTO notificationDTO){
        return new Notification(
                UUID.randomUUID(),
                notificationDTO.getTitle(),
                notificationDTO.getContent(),
                notificationDTO.getUserNotification().getLogin(),
                notificationDTO.getCreateTime(),
                false
        );
    }
}
