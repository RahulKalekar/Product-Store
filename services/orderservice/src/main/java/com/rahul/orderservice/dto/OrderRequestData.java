package com.rahul.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestData {
    private String emailId;
    private String productId;
    private Integer quantity;
}
