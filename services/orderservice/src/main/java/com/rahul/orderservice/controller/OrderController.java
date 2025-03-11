package com.rahul.orderservice.controller;

import com.rahul.orderservice.dto.OrderRequest;
import com.rahul.orderservice.dto.OrderRequestData;
import com.rahul.orderservice.dto.UserInfo;
import com.rahul.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderController.class);
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request,@RequestHeader(name = "X-User-Email") String email) {
        logger.info("Creating order for product: {}", request.getProductId());
        OrderRequestData data = new OrderRequestData(email, request.getProductId(), request.getQuantity());
        try{
            orderService.createOrder(data);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Order creation failed: " + e.getMessage());
        }
        return ResponseEntity.ok("Order placed successfully!");
    }
    @GetMapping("/email")
    public UserInfo getUserInfo(
            @RequestHeader(name = "X-User-Email") String email,
            @RequestHeader(name = "X-User-Name") String name) {
        return new UserInfo(email, name);
    }
    @GetMapping("/logs")
    public String testLogs() {
        log.info("This is an info message");
        log.warn("This is a warning message");
        log.error("This is an error message");
        try {
            throw new RuntimeException("Test exception");
        } catch (Exception e) {
            log.error("Caught an exception", e);
        }
        return "Logs generated";
    }
}
