package com.rahul.orderservice.fallback;

import com.rahul.orderservice.dto.InventoryStatus;
import com.rahul.orderservice.feign.InventoryServiceClient;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceFallback implements InventoryServiceClient {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(InventoryServiceFallback.class);
    @Override
    public InventoryStatus checkStock(String productId, Integer quantity) {
        logger.info("Inventory fallback called");
        InventoryStatus inventoryStatus = new InventoryStatus(false,"OFFLINE");
        return inventoryStatus;
    }

    @Override
    public void decreaseQuantity(String productId, Integer quantity) {
        logger.info("Inventory fallback called");
        //we can make call to stop the order
    }
}
