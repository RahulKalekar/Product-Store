package com.rahul.inventoryservice.controller;

import com.rahul.inventoryservice.entities.Inventory;
import com.rahul.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }
    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }
    @PostMapping
    public ResponseEntity<String> addInventory(@RequestBody Inventory inventory) {
        inventoryService.addInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body("Inventory added successfully.");
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeInventory(@PathVariable String productId) {
        inventoryService.removeInventory(productId);
        return ResponseEntity.ok("Inventory removed successfully.");
    }
    @PostMapping("/decrease/{productId}/{quantity}")
    public ResponseEntity<String> decreaseQuantity(@PathVariable String productId, @PathVariable Integer quantity) {
        inventoryService.decreaseQuantity(productId, quantity);
        return ResponseEntity.ok("Inventory decreased successfully.");
    }
    @PostMapping("/increase/{productId}/{quantity}")
    public ResponseEntity<String> increaseQuantity(@PathVariable String productId, @PathVariable Integer quantity) {
        inventoryService.increaseQuantity(productId, quantity);
        return ResponseEntity.ok("Inventory increased successfully.");
    }
}
