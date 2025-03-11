package com.rahul.orderservice.service;

import com.rahul.orderservice.dto.OrderRequestData;

public interface OrderService {
    void createOrder(OrderRequestData request);
}
