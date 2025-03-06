package com.rahul.orderservice.fallback;

import com.rahul.orderservice.dto.ProductResponse;
import com.rahul.orderservice.feign.ProductServiceClient;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceFallback implements ProductServiceClient {
    @Override
    public ProductResponse getProduct(String productId) {
        return new ProductResponse(productId, "Unavailable", "0.0");
    }
}
