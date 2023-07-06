package com.java.productreactiveservice.handler;


import com.java.productreactiveservice.entity.Product;
import com.java.productreactiveservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductHandler {

    @Autowired
    ProductService productService;


    public Mono<ServerResponse> getAllProductsBy(ServerRequest serverRequest) {
        Flux<Product> productFlux = productService.getAllProducts();
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(productFlux, Product.class);
    }


}
