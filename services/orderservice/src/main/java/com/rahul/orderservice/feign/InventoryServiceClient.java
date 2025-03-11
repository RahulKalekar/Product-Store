package com.rahul.orderservice.feign;

import com.rahul.orderservice.dto.InventoryStatus;
import com.rahul.orderservice.fallback.InventoryServiceFallback;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service",url="http://localhost:8092", fallback = InventoryServiceFallback.class)
public interface InventoryServiceClient {
    @GetMapping("/api/inventory/check/{productId}/{quantity}")
    InventoryStatus checkStock(@PathVariable String productId, @PathVariable Integer quantity);

    @PostMapping("/api/inventory/decrease/{productId}/{quantity}")
    void decreaseQuantity(@PathVariable String productId, @PathVariable Integer quantity);
}
