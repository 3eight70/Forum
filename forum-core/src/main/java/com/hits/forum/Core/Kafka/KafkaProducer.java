package com.hits.forum.Core.Kafka;

import com.hits.common.Core.Message.DTO.MessageNotificationDto;
import com.hits.common.Core.Notification.Enum.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.hits.common.Core.Consts.MESSAGE_TOPIC;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, MessageNotificationDto> kafkaTemplate;

    public void sendMessage(
            UUID userId,
            String title,
            String content,
            List<NotificationChannel> channels,
            Boolean needInHistory){


        MessageNotificationDto notificationDTO = new MessageNotificationDto(
                userId,
                title,
                content,
                channels,
                needInHistory
        );

        kafkaTemplate.send(MESSAGE_TOPIC, notificationDTO);
    }
}
