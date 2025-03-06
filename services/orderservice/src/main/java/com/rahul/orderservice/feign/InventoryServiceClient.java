package com.rahul.orderservice.feign;

import com.rahul.orderservice.fallback.InventoryServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service",url="http://localhost:8092", fallback = InventoryServiceFallback.class)
public interface InventoryServiceClient {
    @GetMapping("/inventory/check/{productId}/{quantity}")
    boolean checkStock(@PathVariable String productId, @PathVariable Integer quantity);
}
