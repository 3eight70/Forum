package com.hits.notification.Core.Notification.Handler;

import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;
import jakarta.mail.MessagingException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MessengerNotificationHandler extends NotificationHandler {
    private static String botToken = "botToken";
    private static String chatId = "chatId";

    @Override
    public void handle(Notification notification, UserNotificationDTO userNotificationDto, List<NotificationChannel> channels) throws MessagingException, UnsupportedEncodingException {
        if (channels.contains(NotificationChannel.MESSENGER)) {
            sendMessage(notification, userNotificationDto);
        }
        if (getNext() != null) {
            getNext().handle(notification, userNotificationDto, channels);
        }
    }

    public void sendMessage(Notification notification, UserNotificationDTO userNotificationDTO) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("https://api.telegram.org/bot" + botToken + "/sendMessage");

            StringEntity params = new StringEntity("{\"chat_id\":\"" + chatId + "\",\"text\":\"" + "Уважаемый "
                    + userNotificationDTO.getLogin() + " для вас пришло уведомление: " + notification.getTitle() + " "
                    + notification.getContent() + "\"}");
            httppost.addHeader("content-type", "application/json");
            httppost.setEntity(params);

            httpclient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
