package com.rahul.inventoryservice.service;

import com.rahul.common.InventoryRequest;
import com.rahul.inventoryservice.dto.InventoryStatus;
import com.rahul.inventoryservice.entities.Inventory;
import com.rahul.inventoryservice.exceptions.InventoryNotFoundException;
import com.rahul.inventoryservice.repo.InventoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Mock
    private InventoryRepo inventoryRepo;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventory = new Inventory("123", 10);  // Example product with id "123" and quantity 10
    }

    @Test
    void decreaseQuantity_ShouldDecreaseInventory_WhenSufficientStock() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.of(inventory));

        inventoryService.decreaseQuantity("123", 5);

        assertEquals(5, inventory.getQuantity());  // After decreasing by 5, the quantity should be 5
        verify(inventoryRepo, times(1)).save(inventory);  // Verify that save was called once
    }

    @Test
    void decreaseQuantity_ShouldThrowException_WhenInsufficientStock() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.of(inventory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                inventoryService.decreaseQuantity("123", 15));

        assertEquals("Insufficient stock for product: 123", exception.getMessage());
        verify(inventoryRepo, times(0)).save(inventory);  // Verify that save was not called
    }

    @Test
    void increaseQuantity_ShouldIncreaseInventory_WhenProductExists() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.of(inventory));

        inventoryService.increaseQuantity("123", 5);

        assertEquals(15, inventory.getQuantity());  // After increasing by 5, the quantity should be 15
        verify(inventoryRepo, times(1)).save(inventory);  // Verify that save was called once
    }

    @Test
    void increaseQuantity_ShouldCreateNewInventory_WhenProductDoesNotExist() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.empty());

        inventoryService.increaseQuantity("123", 5);

        verify(inventoryRepo, times(1)).save(any(Inventory.class));  // Verify that save was called for new inventory
    }

    @Test
    void checkStock_ShouldReturnTrue_WhenSufficientStock() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.of(inventory));

        InventoryStatus status = inventoryService.checkStock("123", 5);

        assertTrue(status.isInStock());
    }

    @Test
    void checkStock_ShouldReturnFalse_WhenInsufficientStock() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.of(inventory));

        InventoryStatus status = inventoryService.checkStock("123", 15);

        assertFalse(status.isInStock());
    }

    @Test
    void getInventoryByProductId_ShouldReturnInventory_WhenProductExists() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.of(inventory));

        Inventory result = inventoryService.getInventoryByProductId("123");

        assertNotNull(result);
        assertEquals("123", result.getProductId());
    }

    @Test
    void getInventoryByProductId_ShouldThrowException_WhenProductDoesNotExist() {
        when(inventoryRepo.findById("123")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                inventoryService.getInventoryByProductId("123"));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void handleProductDeletion_ShouldRemoveInventory() {
        when(inventoryRepo.existsById("123")).thenReturn(true);

        inventoryService.handleProductDeletion("123");

        verify(inventoryRepo, times(1)).deleteById("123");
    }

    @Test
    void handleProductDeletion_ShouldThrowException_WhenProductDoesNotExist() {
        when(inventoryRepo.existsById("123")).thenReturn(false);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class, () ->
                inventoryService.handleProductDeletion("123"));

        assertEquals("Product with ID 123 not found in inventory.", exception.getMessage());
    }

    @Test
    void handleProductCreation_ShouldAddNewInventory() {
        InventoryRequest request = new InventoryRequest("123", 10);

        inventoryService.handleProductCreation(request);

        verify(inventoryRepo, times(1)).save(any(Inventory.class));
    }

    @Test
    void addInventory_ShouldSaveInventory() {
        inventoryService.addInventory(inventory);

        verify(inventoryRepo, times(1)).save(inventory);
    }
}
