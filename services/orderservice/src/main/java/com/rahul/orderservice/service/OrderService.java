package com.rahul.orderservice.service;

import com.rahul.orderservice.dto.OrderRequest;
import org.springframework.stereotype.Service;

public interface OrderService {
    void createOrder(OrderRequest request);
}
