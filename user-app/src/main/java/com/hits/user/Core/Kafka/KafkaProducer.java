package com.hits.user.Core.Kafka;

import com.google.protobuf.Timestamp;
import com.hits.common.Core.Notification.DTO.NotificationDTO;
import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.Notification.Proto.NotificationDTOOuterClass;
import com.hits.common.Core.User.DTO.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static com.hits.common.Core.Consts.TOPIC;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String,
            NotificationDTOOuterClass.NotificationDTO> kafkaTemplate;

    public void sendMessage(
            UserDto userDto,
            String title,
            String content,
            List<NotificationDTOOuterClass.NotificationChannel> channels,
            Boolean needInHistory){

        NotificationDTOOuterClass.NotificationDTO notificationDTO = NotificationDTOOuterClass.NotificationDTO.newBuilder()
                .addAllChannelList(channels)
                .setContent(content)
                .setTitle(title)
                .setNeedInHistory(needInHistory)
                .setUserId(String.valueOf(userDto.getId()))
                .setEmail(userDto.getEmail())
                .setLogin(userDto.getLogin())
                .build();

        kafkaTemplate.send(TOPIC, notificationDTO);
    }
}
