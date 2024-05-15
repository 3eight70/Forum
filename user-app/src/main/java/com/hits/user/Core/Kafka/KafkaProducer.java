package com.hits.user.Core.Kafka;

import com.hits.common.Core.Notification.DTO.NotificationDTO;
import com.hits.common.Core.Notification.Enum.NotificationChannel;
import com.hits.common.Core.User.DTO.UserDto;
import com.hits.common.Core.User.UserNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.hits.common.Core.Consts.TOPIC;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, NotificationDTO> kafkaTemplate;

    public void sendMessage(
            UserDto userDto,
            String title,
            String content,
            List<NotificationChannel> channels,
            Boolean needInHistory){


        NotificationDTO notificationDTO = NotificationDTO.builder()
                .channelList(channels)
                .content(content)
                .title(title)
                .needInHistory(needInHistory)
                .createTime(LocalDateTime.now())
                .userNotification(UserNotificationMapper.userDtoToUserNotificationDto(userDto))
                .build();

        kafkaTemplate.send(TOPIC, notificationDTO);
    }
}
