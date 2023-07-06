package com.java.productreactiveservice.repository;

import com.java.productreactiveservice.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProductRepository  extends ReactiveCrudRepository<Product, Integer> {
    Flux<Product> getProductsByBrand(String brand);

    Mono<Product> getProductByProductId(UUID productId);

    Flux<Product> getProductsByType(String type);

    Mono<Product> getById(Integer id);
}
