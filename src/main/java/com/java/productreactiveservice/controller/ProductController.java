package com.java.productreactiveservice.controller;

import com.java.productreactiveservice.entity.Product;
import com.java.productreactiveservice.kafka.producer.KafkaProductProducer;
import com.java.productreactiveservice.repository.ProductRepository;
import com.java.productreactiveservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/amazon/products")
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    ProductService productService;
    @Autowired
    KafkaProductProducer kafkaProductProducer;
    @Autowired
    private ChannelTopic topic;
    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveTemplate;
    @Autowired
    private ReactiveRedisMessageListenerContainer reactiveMsgListenerContainer;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/save")
    public Mono<Product> saveProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    public Mono<Object> updateAndSaveProduct(Product product) {
        Mono<Product> product1 = productRepository.findById(product.getId());
        return productRepository.findById(product.getId()).map((c) -> {
            c.setPrice(product.getPrice());
            kafkaProductProducer.sendPrice(c);
            return c;
        }).flatMap(c -> productRepository.save(c));
    }

    @PostMapping("/updateprice")
    public void updatePrice(@RequestBody Product product) {


//   this.updateAndSaveProduct(product);

        productService.updateAndSaveProduct(product);
//        return reactiveTemplate.convertAndSend(topic.getTopic(), String.valueOf(product));
    }

    @PutMapping("/update/{id}")
    public void updateProductById(@RequestBody Product product, @PathVariable Integer id) {
//Product product1 = productService.getProductByProductId(id);
//        Product product1;
        LOGGER.info("poduct updated is {}", productRepository.findById(id));
        productRepository.findById(id).map((c) -> {
            c.setPrice(product.getPrice());
            kafkaProductProducer.sendPrice(c);
            return c;


        }).flatMap(c -> {
            LOGGER.info("Price for the product is", c.getPrice());
            productRepository.save(c);

            return Mono.just(c);
        }).subscribe();
    }


    @GetMapping("phone/{brand}")
    public Flux<Product> getProductbyBrand(@PathVariable("brand") String brand) {
        return productService.getProductsByBrand(brand);
    }

    @GetMapping("/allProducts")
    public Flux<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search/{productId}")
    public Mono<Product> getProductByProductId(@PathVariable("productId") UUID productId) {
        return productService.getProductByProductId(productId);
    }

    @GetMapping("/product/{type}")
    public Flux<Product> getProductByType(@PathVariable String type) {
        return productService.getProductsByType(type);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deleteById(@PathVariable Integer id) {
        return productService.deleteById(id);
    }

    @GetMapping("/message/produtcs")
    public Flux<String> receiveProductMessages() {
        LOGGER.info("Starting to receive Product Messages from Channel '" + topic.getTopic() + "'.");
        return reactiveMsgListenerContainer.receive(topic).map(ReactiveSubscription.Message::getMessage).map(msg -> {
            LOGGER.info("New Message received: '" + msg + "'.");
            return msg + "\n";
        });
    }


    @GetMapping("/all/messages")
    public Flux<String> getAll() {
        return productService.subscribeToChannel("pubsub-product-channel");
    }

    @GetMapping("/{id}")
    public Mono<Product> getProduct(@PathVariable Integer id) {
        return productRepository.getById(id);
    }

    public Flux<String> getProducts() {
        return productService.subscribeToChannel("pubsub-product-channel");
    }


}