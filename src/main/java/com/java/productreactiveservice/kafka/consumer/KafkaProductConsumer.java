package com.java.productreactiveservice.kafka.consumer;


import com.java.productreactiveservice.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class KafkaProductConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProductConsumer.class);


    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveTemplate;
    @Autowired
    private ChannelTopic channelTopic;


    @KafkaListener(topics = "amazon-products.1", groupId = "sai-group")
    public void consumePrice(Product product) {
        LOGGER.info("Product recived from kafka {} ", product);
        sendToRedis(product).subscribe();

    }

    public Mono<Long> sendToRedis(Product product) {
        LOGGER.info("Sending to redis product is {}", product);

        return reactiveTemplate.convertAndSend(channelTopic.getTopic(), String.valueOf(product));

    }
}
