package com.hits.notification.Core.Kafka;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.hits.common.Core.Consts.TOPIC;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = TOPIC, groupId = "notifications_group")
    public void listenMessage(NotificationDTO notificationDTO){

    }
}
