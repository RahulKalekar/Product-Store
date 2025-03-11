package com.rahul.orderservice.service;

import com.rahul.common.NotificationDto;
import com.rahul.orderservice.dto.InventoryStatus;
import com.rahul.orderservice.dto.OrderRequestData;
import com.rahul.orderservice.dto.ProductResponse;
import com.rahul.orderservice.entities.Order;
import com.rahul.orderservice.feign.InventoryServiceClient;
import com.rahul.orderservice.feign.ProductServiceClient;
import com.rahul.orderservice.repo.OrderRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

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
        logger.info("Checking inventory for product: {}", request.getProductId());
        checkInventoryStatus(request);
        logger.info("Inventory check passed for product: {}", request.getProductId());

        //step DEduct quantity from inventory
        logger.info("Deducting quantity from inventory for product: {}", request.getProductId());
        inventoryServiceClient.decreaseQuantity(request.getProductId(), request.getQuantity());
        logger.info("Quantity deducted from inventory for product: {}", request.getProductId());

        // Step 2: Fetch product details
        logger.info("Fetching product details for product: {}", request.getProductId());
        ProductResponse product = getProductResponse(request);
        logger.info("Fetched product details for product: {}", request.getProductId());


        // Step 3: Calculate total price
        logger.info("Calculating total price for product: {}", request.getProductId());
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        logger.info("Total price calculated for product: {}", request.getProductId());

        // Step 4: Create the order with "PENDING" status
        logger.info("Creating order for product: {}", request.getProductId());
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.getEmailId(), request.getProductId(), request.getQuantity(), totalPrice, "PENDING");
        orderRepo.save(order);
        logger.info("Order created for product: {}", request.getProductId());


        //we can have payment stage here, but we don't have so confirmed it
        // Step 5: Update order status to "CONFIRMED"
        order.setStatus("CONFIRMED");
        orderRepo.save(order);
        logger.info("Order confirmed for product: {}", request.getProductId());


        // Step 6: Send Kafka notification
        logger.info("Sending Kafka notification for order: {}", orderId);
        sendNotification(request, orderId, product, totalPrice);
        logger.info("Sent Kafka notification for order: {}", orderId);
    }

    private void sendNotification(OrderRequestData request, String orderId, ProductResponse product, BigDecimal totalPrice) {
        NotificationDto notification = new NotificationDto(request.getEmailId(), orderId, request.getQuantity(), product.getName(), totalPrice);
        kafkaTemplate.send("order-notifications", notification);
    }

    private ProductResponse getProductResponse(OrderRequestData request) {
        ProductResponse product = productServiceClient.getProduct(request.getProductId());
        if ("UnavailableDown".equals(product.getName())) {
            throw new RuntimeException("ProductService is down ");
        }
        if ("Unavailable".equals(product.getName())) {
            throw new RuntimeException("Product not found: " + request.getProductId());
        }

        return product;
    }
    private void checkInventoryStatus(OrderRequestData request) {
        InventoryStatus inventoryStatus = inventoryServiceClient.checkStock(request.getProductId(), request.getQuantity());

        if(inventoryStatus.getStatus().equals("OFFLINE")) {
            throw new RuntimeException("The inventory service is offline: ");
        }
        if (!inventoryStatus.isInStock()) {
            throw new RuntimeException("Insufficient stock for product: " + request.getProductId());
        }
    }

}