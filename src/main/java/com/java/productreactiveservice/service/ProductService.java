package com.java.productreactiveservice.service;


import com.java.productreactiveservice.entity.Product;
import com.java.productreactiveservice.kafka.producer.KafkaProductProducer;
import com.java.productreactiveservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    ProductRepository productRepository;
    @Autowired
    KafkaProductProducer kafkaProductProducer;
    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveTemplate;
    @Autowired
    private ChannelTopic channelTopic;

    public Mono<Product> addProduct(Product product) {
        if (product.productId == null) {
            product.setProductId(UUID.randomUUID());
        }
        LOGGER.info("Inside Add Product ,product is {}", product);
        return productRepository.save(product);
    }


    public Flux<String> subscribeToChannel(String channel) {
        Flux<String> messageFlux = reactiveTemplate.listenToChannel(channel).map(message -> message.getMessage());

        messageFlux.subscribe(content -> {
            LOGGER.info("Recevied message" + content + " from channel : ", channel);


        });
        return messageFlux;
    }

    public Flux<Product> getProductsByBrand(String brand) {
        return productRepository.getProductsByBrand(brand);
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> getProductByProductId(UUID productId) {
        return productRepository.getProductByProductId(productId);
    }

    public Flux<Product> getProductsByType(String type) {
        return productRepository.getProductsByType(type);
    }

    public Mono<Void> deleteById(Integer id) {
        return productRepository.deleteById(id);
    }

    public void updateAndSaveProduct(Product product) {
        Integer id = product.getId();
        Mono<Product> product1 = productRepository.getById(id);

        LOGGER.info("Updating price for the product {}", id);
        LOGGER.info("Updating product is {}", product1);
        saveToDb(product, product1);
    }

    public void saveToDb(Product product, Mono<Product> productMono) {
        productMono.map((c) -> {
            c.setPrice(product.getPrice());
            LOGGER.info("upated product is ", c);
            kafkaProductProducer.sendPrice(c);
            return c;
        }).flatMap(c -> {
            addProduct(c);
            return Mono.just(c);
        }).subscribe();
    }


}
