package com.rahul.inventoryservice.exceptions;

import com.rahul.inventoryservice.dto.ErrorDetails;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInventoryNotFoundException() {
        InventoryNotFoundException ex = new InventoryNotFoundException("Inventory item not found");

        ResponseEntity<ErrorDetails> response = handler.handleInventoryNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Inventory item not found", response.getBody().getMessage());
        assertEquals("Inventory not found", response.getBody().getDetails());
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorDetails> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertEquals("Invalid input", response.getBody().getDetails());
    }
}