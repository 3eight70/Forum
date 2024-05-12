package com.hits.notification.Core.Kafka;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserNotificationDto;
import com.hits.notification.Core.Notification.Entity.Notification;
import com.hits.notification.Core.Notification.Mapper.NotificationMapper;
import com.hits.notification.Core.Notification.Repository.NotificationRepository;
import com.hits.notification.Core.Notification.Service.SenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.internals.Sender;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.hits.common.Core.Consts.TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationRepository notificationRepository;
    private final SenderService sender;

    @KafkaListener(topics = TOPIC, groupId = "notifications_group")
    public void listenMessage(
            NotificationDTO notificationDTO,
            UserNotificationDto userNotificationDto,
            List<NotificationChannel> deliveryChannels,
            Boolean needInHistory) throws MessagingException, UnsupportedEncodingException {

        Notification notification = NotificationMapper
                .notificationDtoToNotification(notificationDTO);

        if (needInHistory){
            notificationRepository.save(notification);
        }

        sender.sendNotification(notification, userNotificationDto, deliveryChannels);
    }
}
