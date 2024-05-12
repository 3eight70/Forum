package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDto;
import com.hits.notification.Core.Notification.Entity.Notification;
import jakarta.mail.MessagingException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class NotificationHandlerChain {
    private NotificationHandler head;

    public NotificationHandlerChain() {
        JavaMailSender mailSender = new JavaMailSenderImpl();
        addHandler(new EmailNotificationHandler(mailSender));
        addHandler(new MessengerNotificationHandler()); //Пустышка
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

    public void process(Notification notification, UserNotificationDto userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        head.handle(notification, userNotificationDto, channels);
    }
}
