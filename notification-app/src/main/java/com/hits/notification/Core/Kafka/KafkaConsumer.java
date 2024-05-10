package com.hits.notification.Core.Kafka;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "notification-topic", groupId = "notifications_group")
    public void listenMessage(NotificationDTO notificationDTO){

    }
}
