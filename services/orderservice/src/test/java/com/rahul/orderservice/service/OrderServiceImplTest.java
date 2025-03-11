package com.rahul.orderservice.service;

import com.rahul.common.NotificationDto;
import com.rahul.orderservice.dto.InventoryStatus;
import com.rahul.orderservice.dto.OrderRequestData;
import com.rahul.orderservice.dto.ProductResponse;
import com.rahul.orderservice.entities.Order;
import com.rahul.orderservice.feign.InventoryServiceClient;
import com.rahul.orderservice.feign.ProductServiceClient;
import com.rahul.orderservice.repo.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @Mock
    private InventoryStatus inventoryStatus;

    @Mock
    private ProductResponse productResponse;

    private OrderRequestData orderRequestData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock data for OrderRequestData
        orderRequestData = new OrderRequestData();
        orderRequestData.setEmailId("test@example.com");
        orderRequestData.setProductId("12345");
        orderRequestData.setQuantity(2);
    }

    @Test
    void createOrder_ShouldCreateAndConfirmOrder() {
        // Mock inventory service response
        when(inventoryServiceClient.checkStock(any(), anyInt())).thenReturn(inventoryStatus);
        when(inventoryStatus.isInStock()).thenReturn(true);
        when(inventoryStatus.getStatus()).thenReturn("ONLINE");

        // Mock product service response
        when(productServiceClient.getProduct(any())).thenReturn(productResponse);
        when(productResponse.getPrice()).thenReturn(BigDecimal.valueOf(100));
        when(productResponse.getName()).thenReturn("Product Name");

        // Mock orderRepo.save() to return the saved order (if needed for testing)
        Order mockOrder = new Order("orderId", "test@example.com", "12345", 2, BigDecimal.valueOf(200), "PENDING");
        when(orderRepo.save(any(Order.class))).thenReturn(mockOrder);

        // Call the method under test
        orderService.createOrder(orderRequestData);

        // Verify interactions with mocked dependencies
        verify(inventoryServiceClient, times(1)).checkStock(any(), anyInt());
        verify(productServiceClient, times(1)).getProduct(any());
        verify(orderRepo, times(2)).save(any(Order.class)); // Once for "PENDING", once for "CONFIRMED"
        verify(kafkaTemplate, times(1)).send(eq("order-notifications"), any(NotificationDto.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenInventoryIsOffline() {
        // Mock inventory service response with OFFLINE status
        when(inventoryServiceClient.checkStock(any(), anyInt())).thenReturn(inventoryStatus);
        when(inventoryStatus.getStatus()).thenReturn("OFFLINE");

        // Call the method and verify exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequestData));
        assertEquals("The inventory service is offline: ", exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductServiceFails() {
        // Mock inventory service response
        when(inventoryServiceClient.checkStock(any(), anyInt())).thenReturn(inventoryStatus);
        when(inventoryStatus.isInStock()).thenReturn(true);
        when(inventoryStatus.getStatus()).thenReturn("ONLINE");

        // Mock product service to simulate failure
        when(productServiceClient.getProduct(any())).thenReturn(productResponse);
        when(productResponse.getName()).thenReturn("Unavailable");

        // Call the method and verify exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequestData));
        assertEquals("Product not found: 12345", exception.getMessage());
    }

    @Test
    void createOrder_ShouldHandleProductServiceDown() {
        // Mock inventory service response
        when(inventoryServiceClient.checkStock(any(), anyInt())).thenReturn(inventoryStatus);
        when(inventoryStatus.isInStock()).thenReturn(true);
        when(inventoryStatus.getStatus()).thenReturn("ONLINE");

        // Mock product service to simulate service being down
        when(productServiceClient.getProduct(any())).thenReturn(productResponse);
        when(productResponse.getName()).thenReturn("UnavailableDown");

        // Call the method and verify exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequestData));
        assertEquals("ProductService is down ", exception.getMessage());
    }
}
