package com.rahul.orderservice.service;

import com.rahul.common.NotificationDto;
import com.rahul.orderservice.dto.OrderRequestData;
import com.rahul.orderservice.dto.ProductResponse;
import com.rahul.orderservice.entities.Order;
import com.rahul.orderservice.feign.InventoryServiceClient;
import com.rahul.orderservice.feign.ProductServiceClient;
import com.rahul.orderservice.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private InventoryServiceClient inventoryServiceClient;
    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private KafkaTemplate<String, NotificationDto> kafkaTemplate;

    @Override
    public void createOrder(OrderRequestData request) {
        // Step 1: Check inventory
        boolean isStockAvailable = inventoryServiceClient.checkStock(request.getProductId(), request.getQuantity());
        if (!isStockAvailable) {
            throw new RuntimeException("Insufficient stock for product: " + request.getProductId());
        }

        // Step 2: Fetch product details
        ProductResponse product = productServiceClient.getProduct(request.getProductId());
        if (product == null || "Unavailable".equals(product.getName())) {
            throw new RuntimeException("Product not found: " + request.getProductId());
        }

        // Step 3: Calculate total price
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        // Step 4: Create the order with "PENDING" status
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.getEmailId(), request.getProductId(), request.getQuantity(), totalPrice, "PENDING");
        orderRepo.save(order);

        // Step 5: Update order status to "CONFIRMED"
        order.setStatus("CONFIRMED");
        orderRepo.save(order);

        // Step 6: Send Kafka notification
        NotificationDto notification = new NotificationDto(request.getEmailId(),orderId, request.getQuantity(), product.getName(), totalPrice);
        kafkaTemplate.send("order-notifications", notification);
        System.out.println("sent kafka notify");
    }
}
