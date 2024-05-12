package com.hits.user.Core.Kafka;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import com.hits.common.Core.User.DTO.UserDto;
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
            UserDto userDto,
            String title,
            String content,
            LocalDateTime createTime){
        kafkaTemplate.send(TOPIC,
                new NotificationDTO(title, content, userDto.getLogin(), createTime));
    }
}
