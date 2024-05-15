package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class NotificationHandlerChain {
    private NotificationHandler head;

    @Autowired
    public NotificationHandlerChain(JavaMailSender javaMailSender) {
        addHandler(new EmailNotificationHandler(javaMailSender));
        addHandler(new MessengerNotificationHandler());
    }

    public void addHandler(NotificationHandler handler) {
        if (head == null) {
            head = handler;
        } else {
            NotificationHandler currentHandler = head;
            while (currentHandler.getNext() != null) {
                currentHandler = currentHandler.getNext();
            }
            currentHandler.setNext(handler);
        }
    }

    public void process(Notification notification, UserNotificationDTO userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        head.handle(notification, userNotificationDto, channels);
    }
}
