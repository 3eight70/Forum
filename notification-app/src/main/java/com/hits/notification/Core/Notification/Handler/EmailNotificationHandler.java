package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailNotificationHandler extends NotificationHandler {
    private final JavaMailSender javaMailSender;

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

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String fromAddress = "gbhfns47@gmail.com";
        helper.setFrom(fromAddress, senderName);
        helper.setTo(userMailDto.getEmail());
        helper.setSubject(notification.getTitle());
        helper.setText(notification.getContent(), true);

        javaMailSender.send(message);
    }
}

