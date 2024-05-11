package com.hits.notification.Core.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hits.common.Core.Consts.TOPIC;

@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic newTopic(){
        return new NewTopic(
                TOPIC,
                1,
                (short) 1
        );
    }
}
