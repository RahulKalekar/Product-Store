package com.rahul.productservice.exceptions;

import com.rahul.productservice.dto.ErrorDetails;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");

        ResponseEntity<ErrorDetails> response = handler.handleProductNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody().getMessage());
        assertEquals("Product not found", response.getBody().getDetails());
    }

    @Test
    void handleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "field", "default message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("default message", response.getBody().get("field"));
    }

    @Test
    void handleDatabaseExceptions() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Database error");

        ResponseEntity<ErrorDetails> response = handler.handleDatabaseExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody().getMessage());
        assertEquals("Database error", response.getBody().getDetails());
    }

    @Test
    void handleGlobalException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorDetails> response = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().getMessage());
        assertEquals("An unexpected error occurred", response.getBody().getDetails());
    }
}