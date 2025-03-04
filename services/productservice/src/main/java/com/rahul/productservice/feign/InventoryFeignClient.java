package com.rahul.productservice.feign;

import com.rahul.productservice.dto.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", url = "http://localhost:8092")
public interface InventoryFeignClient {
    
    @PostMapping("/inventory")
    void createInventory(InventoryRequest inventoryRequest);

    @DeleteMapping("/inventory/{productId}")
    void deleteInventory(@PathVariable String productId);
}
