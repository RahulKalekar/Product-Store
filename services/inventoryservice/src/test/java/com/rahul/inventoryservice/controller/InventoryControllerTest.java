package com.rahul.inventoryservice.controller;

import com.rahul.inventoryservice.dto.InventoryStatus;
import com.rahul.inventoryservice.entities.Inventory;
import com.rahul.inventoryservice.exceptions.InventoryNotFoundException;
import com.rahul.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllInventory_Success() {
        // Arrange
        List<Inventory> expectedInventory = Arrays.asList(
                new Inventory("1", 10),
                new Inventory("2", 20)
        );
        when(inventoryService.getAllInventory()).thenReturn(expectedInventory);

        // Act
        ResponseEntity<List<Inventory>> response = inventoryController.getAllInventory();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedInventory, response.getBody());
        verify(inventoryService).getAllInventory();
    }

    @Test
    void getInventoryByProductId_Success() {
        // Arrange
        String productId = "1";
        Inventory expected = new Inventory(productId, 10);
        when(inventoryService.getInventoryByProductId(productId)).thenReturn(expected);

        // Act
        ResponseEntity<Inventory> response = inventoryController.getInventoryByProductId(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(inventoryService).getInventoryByProductId(productId);
    }

    @Test
    void addInventory_Success() {
        // Arrange
        Inventory inventory = new Inventory("1", 10);
        doNothing().when(inventoryService).addInventory(any(Inventory.class));

        // Act
        ResponseEntity<String> response = inventoryController.addInventory(inventory);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Inventory added successfully.", response.getBody());
        verify(inventoryService).addInventory(inventory);
    }

    @Test
    void removeInventory_Success() {
        // Arrange
        String productId = "1";
        doNothing().when(inventoryService).removeInventory(productId);

        // Act
        ResponseEntity<String> response = inventoryController.removeInventory(productId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory removed successfully.", response.getBody());
        verify(inventoryService).removeInventory(productId);
    }

    @Test
    void decreaseQuantity_Success() {
        // Arrange
        String productId = "1";
        Integer quantity = 5;
        doNothing().when(inventoryService).decreaseQuantity(productId, quantity);

        // Act
        ResponseEntity<String> response = inventoryController.decreaseQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory decreased successfully.", response.getBody());
        verify(inventoryService).decreaseQuantity(productId, quantity);
    }

    @Test
    void increaseQuantity_Success() {
        // Arrange
        String productId = "1";
        Integer quantity = 5;
        doNothing().when(inventoryService).increaseQuantity(productId, quantity);

        // Act
        ResponseEntity<String> response = inventoryController.increaseQuantity(productId, quantity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory increased successfully.", response.getBody());
        verify(inventoryService).increaseQuantity(productId, quantity);
    }

    @Test
    void checkStock_Success() {
        // Arrange
        String productId = "1";
        Integer quantity = 5;
        InventoryStatus expectedStatus = new InventoryStatus();
        expectedStatus.setInStock(true);
        when(inventoryService.checkStock(productId, quantity)).thenReturn(expectedStatus);

        // Act
        ResponseEntity<InventoryStatus> response = inventoryController.checkStock(productId, quantity);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isInStock());
        verify(inventoryService).checkStock(productId, quantity);
    }

    @Test
    void getInventoryByProductId_NotFound() {
        // Arrange
        String productId = "nonexistent";
        when(inventoryService.getInventoryByProductId(productId))
                .thenThrow(new InventoryNotFoundException("Product not found"));

        // Act & Assert
        assertThrows(InventoryNotFoundException.class, () ->
                inventoryController.getInventoryByProductId(productId));
        verify(inventoryService).getInventoryByProductId(productId);
    }

    @Test
    void decreaseQuantity_InsufficientStock() {
        // Arrange
        String productId = "1";
        Integer quantity = 100;
        doThrow(new IllegalArgumentException("Insufficient stock"))
                .when(inventoryService).decreaseQuantity(productId, quantity);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                inventoryController.decreaseQuantity(productId, quantity));
        verify(inventoryService).decreaseQuantity(productId, quantity);
    }
}