package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;

import java.util.List;

public class MessengerNotificationHandler extends NotificationHandler {  //Пустышка для того, чтобы показать, что дополнительные каналы добавить не сложно
    @Override
    public void handle(Notification notification, UserNotificationDTO userNotificationDto, List<NotificationChannel> channels) {

    }
}
