package com.hits.notification.Core.Notification.Service;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDto;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Handler.EmailNotificationHandler;
import com.hits.notification.Core.Notification.Handler.MessengerNotificationHandler;
import com.hits.notification.Core.Notification.Handler.NotificationHandlerChain;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.hits.common.Core.Consts.VERIFY_USER;

@Service
@RequiredArgsConstructor
public class SenderService {
    private final NotificationHandlerChain handlerChain;

    public void sendNotification(Notification notification, UserNotificationDto userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        handlerChain.process(notification, userNotificationDto, channels);
    }
}
