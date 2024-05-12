package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RequiredArgsConstructor
public class EmailNotificationHandler extends NotificationHandler {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override
    public void handle(Notification notification, UserNotificationDTO userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        if (channels.contains(NotificationChannel.EMAIL)) {
            sendEmail(notification, userNotificationDto);
        }
        if (getNext() != null) {
            getNext().handle(notification, userNotificationDto, channels);
        }
    }

    private void sendEmail(Notification notification, UserNotificationDTO userMailDto)
            throws UnsupportedEncodingException, MessagingException {
        String senderName = "HITS.CO";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(userMailDto.getEmail());
        helper.setSubject(notification.getTitle());
        helper.setText(notification.getContent(), true);

        mailSender.send(message);
    }
}

