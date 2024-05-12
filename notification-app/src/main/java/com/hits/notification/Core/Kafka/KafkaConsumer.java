package com.hits.notification.Core.Kafka;

import com.google.protobuf.Timestamp;
import com.hits.common.Core.Notification.DTO.NotificationDTO;
import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.Notification.Proto.NotificationDTOOuterClass;
import com.hits.common.Core.User.DTO.UserNotificationDTO;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Mapper.NotificationMapper;
import com.hits.notification.Core.Notification.Repository.NotificationRepository;
import com.hits.notification.Core.Notification.Service.SenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;

import static com.hits.common.Core.Consts.*;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationRepository notificationRepository;
    private final SenderService sender;

    @KafkaListener(topics = TOPIC, groupId = NOTIFICATION_GROUP)
    public void listenMessage(byte[] data)
            throws MessagingException, UnsupportedEncodingException,  com.google.protobuf.InvalidProtocolBufferException {
        NotificationDTOOuterClass.NotificationDTO notificationProto = NotificationDTOOuterClass.NotificationDTO.parseFrom(data);
        NotificationDTOOuterClass.UserNotificationDTO userNotificationDTO = notificationProto.getUserNotification();

        List<NotificationChannel> channels = new ArrayList<>();

        for (NotificationDTOOuterClass.NotificationChannel channel : notificationProto.getChannelListList()){
            if (channel == NotificationDTOOuterClass.NotificationChannel.EMAIL){
                channels.add(NotificationChannel.EMAIL);
            }
            else if (channel == NotificationDTOOuterClass.NotificationChannel.MESSENGER){
                channels.add(NotificationChannel.MESSENGER);
            }
        }

        UserNotificationDTO userNotification = new UserNotificationDTO(
                UUID.fromString(userNotificationDTO.getUserId()),
                userNotificationDTO.getEmail(),
                userNotificationDTO.getLogin()
        );

        NotificationDTO notificationDTO = new NotificationDTO(
                notificationProto.getTitle(),
                notificationProto.getContent(),
                userNotification,
                convertTimestampToDateTime(notificationProto.getCreateTime()),
                channels,
                notificationProto.getNeedInHistory()
        );

        Notification notification = NotificationMapper
                .notificationDtoToNotification(notificationDTO);

        log.info("Получено сообщение с title: " + notification.getTitle());

        if (notificationDTO.getNeedInHistory()){
            notificationRepository.save(notification);
        }

        sender.sendNotification(notification, notificationDTO.getUserNotification(), notificationDTO.getChannelList());
    }

    private static LocalDateTime convertTimestampToDateTime(Timestamp timestamp) {
        long seconds = timestamp.getSeconds();
        int nanos = timestamp.getNanos();

        Instant instant = Instant.ofEpochSecond(seconds, nanos);

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
