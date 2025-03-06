package com.rahul.orderservice.fallback;

import com.rahul.orderservice.feign.InventoryServiceClient;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceFallback implements InventoryServiceClient {
    @Override
    public boolean checkStock(String productId, Integer quantity) {
        return false; // Assume stock is unavailable when Inventory Service is down
    }
}
