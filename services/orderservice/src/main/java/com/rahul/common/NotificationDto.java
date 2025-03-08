package com.rahul.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private String emailId;
    private String orderId;
    private Integer quantity;
    private String productName;
    private BigDecimal totalPrice;
}
