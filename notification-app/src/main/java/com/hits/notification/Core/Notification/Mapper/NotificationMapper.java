package com.hits.notification.Core.Notification.Mapper;

import com.hits.notification.Core.Notification.DTO.NotificationForUserModel;
import com.hits.notification.Core.Notification.Entity.Notification;

public final class NotificationMapper {
    public static NotificationForUserModel notificationToNotificationForUser(Notification notification){
        return new NotificationForUserModel(
                notification.getTitle(),
                notification.getContent(),
                notification.getCreateTime(),
                notification.getIsRead()
        );
    }
}
