package com.rahul.orderservice.controller;

import com.rahul.orderservice.dto.OrderRequest;
import com.rahul.orderservice.dto.OrderRequestData;
import com.rahul.orderservice.dto.UserInfo;
import com.rahul.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setProductId("123");
        request.setQuantity(2);
        String userEmail = "test@example.com";

        // Act
        ResponseEntity<String> response = orderController.createOrder(request, userEmail);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order placed successfully!", response.getBody());
        verify(orderService, times(1)).createOrder(any(OrderRequestData.class));
    }

    @Test
    void createOrder_Failure() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setProductId("123");
        request.setQuantity(2);
        String userEmail = "test@example.com";

        doThrow(new RuntimeException("Service error"))
                .when(orderService).createOrder(any(OrderRequestData.class));

        // Act
        ResponseEntity<String> response = orderController.createOrder(request, userEmail);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Order creation failed"));
        verify(orderService, times(1)).createOrder(any(OrderRequestData.class));
    }

    @Test
    void getUserInfo_Success() {
        // Arrange
        String email = "test@example.com";
        String name = "Test User";

        // Act
        UserInfo userInfo = orderController.getUserInfo(email, name);

        // Assert
        assertNotNull(userInfo);
        assertEquals(email, userInfo.getEmail());
        assertEquals(name, userInfo.getName());
    }
}