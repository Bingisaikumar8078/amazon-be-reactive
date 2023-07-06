package com.java.productreactiveservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaTopicConfig {

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name("amazon-products.1")
                .build();
    }
}
