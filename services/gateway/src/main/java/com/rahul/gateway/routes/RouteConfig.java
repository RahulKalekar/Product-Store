package com.rahul.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/products/**")
                        .uri(productServiceUrl))
                .route("order-service", r -> r.path("/api/orders/**")
                        .uri(orderServiceUrl))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri(inventoryServiceUrl))

                // Swagger Routes
                .route("product-service-swagger", r -> r.path("/aggregate/product-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/product-service/v3/api-docs", "/v3/api-docs"))
                        .uri(productServiceUrl))

                .route("order-service-swagger", r -> r.path("/aggregate/order-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/order-service/v3/api-docs", "/v3/api-docs"))
                        .uri(orderServiceUrl))
                .route("inventory-service-swagger", r -> r.path("/aggregate/inventory-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/inventory-service/v3/api-docs", "/v3/api-docs"))
                        .uri(inventoryServiceUrl))

                .build();
    }
}
