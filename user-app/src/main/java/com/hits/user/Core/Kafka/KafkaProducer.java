package com.hits.user.Core.Kafka;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.hits.common.Core.Consts.TOPIC;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, NotificationDTO> kafkaTemplate;

    public void sendMessage(
            String title,
            String content,
            String userLogin,
            LocalDateTime createTime){
        kafkaTemplate.send(TOPIC, new NotificationDTO(title, content, userLogin, createTime));
    }
}
