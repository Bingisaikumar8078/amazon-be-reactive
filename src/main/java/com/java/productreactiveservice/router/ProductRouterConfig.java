package com.java.productreactiveservice.router;


import com.java.productreactiveservice.handler.ProductHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouterConfig {

    @Autowired
    private ProductHandler productHandler;


    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/v1/amazon/products/all", productHandler::getAllProductsBy)
                .build();
    }

}
