package com.hits.notification.Core.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hits.common.Core.Notification.DTO.NotificationDTO;
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

import static com.hits.common.Core.Consts.NOTIFICATION_GROUP;
import static com.hits.common.Core.Consts.TOPIC;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationRepository notificationRepository;
    private final SenderService sender;

    @KafkaListener(topics = TOPIC, groupId = NOTIFICATION_GROUP, containerFactory = "singleFactory")
    public void listenMessage(NotificationDTO notificationDTO)
            throws MessagingException, UnsupportedEncodingException {

        Notification notification = NotificationMapper
                .notificationDtoToNotification(notificationDTO);

        log.info("Получено сообщение с title: " + notification.getTitle());

        if (notificationDTO.getNeedInHistory()){
            notificationRepository.save(notification);
        }

        sender.sendNotification(notification, notificationDTO.getUserNotification(), notificationDTO.getChannelList());
    }

}
