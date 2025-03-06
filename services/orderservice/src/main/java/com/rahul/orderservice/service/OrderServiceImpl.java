package com.rahul.orderservice.service;

import com.rahul.orderservice.dto.*;
import com.rahul.orderservice.entities.Order;
import com.rahul.orderservice.feign.InventoryServiceClient;
import com.rahul.orderservice.feign.ProductServiceClient;
import com.rahul.orderservice.repo.OrderRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final InventoryServiceClient inventoryServiceClient;
    private final ProductServiceClient productServiceClient;

    private final OrderRepo orderRepo;

    @Override
    public void createOrder(OrderRequest request) {
        // Step 1: Check inventory
        boolean isStockAvailable = inventoryServiceClient.checkStock(request.getProductId(), request.getQuantity());
        if (!isStockAvailable) {
            throw new RuntimeException("Insufficient stock for product: " + request.getProductId());
        }

        // Step 2: Create the order with "PENDING" status
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.getProductId(), request.getQuantity(), "PENDING");
        orderRepo.save(order);

        // Step 3: Fetch product details
        ProductResponse product = productServiceClient.getProduct(request.getProductId());
        if (product == null || "Unavailable".equals(product.getName())) {
            throw new RuntimeException("Product not found: " + request.getProductId());
        }

        // Step 4: Update order status to "CONFIRMED"
        order.setStatus("CONFIRMED");
        orderRepo.save(order);
    }

}
