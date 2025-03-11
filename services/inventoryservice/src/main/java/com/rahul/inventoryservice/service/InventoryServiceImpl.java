package com.rahul.inventoryservice.service;

import com.rahul.common.InventoryRequest;
import com.rahul.inventoryservice.dto.InventoryStatus;
import com.rahul.inventoryservice.entities.Inventory;
import com.rahul.inventoryservice.exceptions.InventoryNotFoundException;
import com.rahul.inventoryservice.repo.InventoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;

    @Override
    public void decreaseQuantity(String productId, Integer quantity) {
        Inventory inventory = inventoryRepo.findById(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Product not found in inventory"));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepo.save(inventory);
    }

    @Override
    public void increaseQuantity(String productId, Integer quantity) {
        Inventory inventory = inventoryRepo.findById(productId)
                .orElse(new Inventory(productId, 0));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepo.save(inventory);
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepo.findAll();
    }

    @Override
    public InventoryStatus checkStock(String productId, Integer quantity) {
        Inventory inventory = getInventoryByProductId(productId); //gets the inventory
        InventoryStatus InventoryStatus = new InventoryStatus();
        // check quantitiy
        // if yes then true
        InventoryStatus.setInStock(inventory.getQuantity() >= quantity);
        return InventoryStatus;
    }
    @Override
    public Inventory getInventoryByProductId(String productId) {
        return inventoryRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @KafkaListener(topics = "product-inventory-deletion", groupId = "inventory-group-id")
    public void handleProductDeletion(String productId) {
        removeInventory(productId);
    }
    @Override
    public void removeInventory(String productId) {
        if (!inventoryRepo.existsById(productId)) {
            throw new InventoryNotFoundException("Product with ID " + productId + " not found in inventory.");
        }
        inventoryRepo.deleteById(productId);
    }
    @KafkaListener(topics = "product-inventory-creation", groupId = "inventory-group-id")
    public void handleProductCreation(InventoryRequest inventoryRequest) {
        addInventory(new Inventory(inventoryRequest.getProductId(), inventoryRequest.getQuantity()));
    }
    @Override
    public void addInventory(Inventory inventory) {
        inventoryRepo.save(inventory);
    }

}

