package com.java.productreactiveservice.kafka.producer;

import com.java.productreactiveservice.entity.Product;
import com.java.productreactiveservice.repository.ProductRepository;
import com.java.productreactiveservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class KafkaProductProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProductProducer.class);


    private KafkaTemplate<Product, Product> kafkaTemplate;

    public KafkaProductProducer(KafkaTemplate<Product, Product> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Autowired
    private ProductRepository productRepository;

    public void sendPrice(Product product) {
        LOGGER.info("Price is {}", product.getPrice());



        kafkaTemplate.send("amazon-products.1", product);
//        productService.sendToRedis(product);
    }



}
