package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDto;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Service.SenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.hits.common.Core.Consts.VERIFY_USER;

@RequiredArgsConstructor
public class EmailNotificationHandler extends NotificationHandler {
    private final JavaMailSender mailSender;

    @Override
    public void handle(Notification notification, UserNotificationDto userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        if (channels.contains(NotificationChannel.EMAIL)) {
            sendVerificationEmail(notification, userNotificationDto);
        }
        if (getNext() != null) {
            getNext().handle(notification, userNotificationDto, channels);
        }
    }

    private void sendVerificationEmail(Notification notification, UserNotificationDto userMailDto)
            throws UnsupportedEncodingException, MessagingException {
        String toAddress = userMailDto.getEmail();
        String fromAddress = "gbhfns47@gmail.com";
        String senderName = "HITS.CO";
        String subject = "Пожалуйста подтвердите свою регистрацию";
        String content = "Эй, [[name]],<br>"
                + "Пожалуйста перейдите по ссылке ниже для подтверждения регистрации:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ПОДТВЕРДИ МЕНЯ</a></h3>"
                + "Спасибо,<br>"
                + "HITS COMPANY.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", notification.getUserLogin());
        String verifyURL = "http://localhost:8080" + VERIFY_USER + "?id=" + userMailDto.getUserId() + "&code=" + userMailDto.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
}

