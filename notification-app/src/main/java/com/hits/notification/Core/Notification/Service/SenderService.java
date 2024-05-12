package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Handler.NotificationHandlerChain;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SenderService {
    private final NotificationHandlerChain handlerChain;

    public void sendNotification(Notification notification, UserNotificationDTO userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        handlerChain.process(notification, userNotificationDto, channels);
    }
}
