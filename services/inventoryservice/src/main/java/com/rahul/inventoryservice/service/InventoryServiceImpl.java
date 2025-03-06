package com.rahul.inventoryservice.service;

import com.rahul.inventoryservice.entities.Inventory;
import com.rahul.inventoryservice.exceptions.InventoryNotFoundException;
import com.rahul.inventoryservice.repo.InventoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;

    @Override
    public void addInventory(Inventory inventory) {
        inventoryRepo.save(inventory);
    }

    @Override
    public void removeInventory(String productId) {
        if (!inventoryRepo.existsById(productId)) {
            throw new InventoryNotFoundException("Product with ID " + productId + " not found in inventory.");
        }
        inventoryRepo.deleteById(productId);
    }
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
    public Inventory getInventoryByProductId(String productId) {
        return inventoryRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepo.findAll();
    }

    @Override
    public boolean checkStock(String productId, Integer quantity) {
        return inventoryRepo.existsById(productId) && inventoryRepo.findById(productId).get().getQuantity() >= quantity;
    }


}

