package com.hits.user.Core.Kafka;

import com.hits.common.Core.Message.DTO.MessageNotificationDto;
import com.hits.common.Exceptions.NotFoundException;
import com.hits.user.Core.User.Entity.User;
import com.hits.user.Core.User.Mapper.UserMapper;
import com.hits.user.Core.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.hits.common.Core.Consts.MESSAGE_TOPIC;
import static com.hits.common.Core.Consts.NOTIFICATION_GROUP;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    @KafkaListener(topics = MESSAGE_TOPIC, groupId = NOTIFICATION_GROUP, containerFactory = "singleFactory")
    public void listenMessage(MessageNotificationDto messageNotificationDto){
        UUID userId = messageNotificationDto.getUserId();
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id=%s не существует", userId)));

        kafkaProducer.sendMessage(
                UserMapper.userToUserDto(user),
                messageNotificationDto.getTitle(),
                messageNotificationDto.getContent(),
                messageNotificationDto.getChannels(),
                messageNotificationDto.getNeedInHistory()
        );
    }
}
