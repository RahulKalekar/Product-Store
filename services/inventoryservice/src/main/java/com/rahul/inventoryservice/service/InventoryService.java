package com.rahul.inventoryservice.service;

import com.rahul.inventoryservice.entities.Inventory;
import com.rahul.inventoryservice.repo.InventoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {

    void removeInventory(String productId);
    void addInventory(Inventory inventory);
    void decreaseQuantity(String productId, Integer quantity);
    void increaseQuantity(String productId, Integer quantity);
    Inventory getInventoryByProductId(String productId);
    List<Inventory> getAllInventory();

}
