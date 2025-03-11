package com.rahul.orderservice.fallback;

import com.rahul.orderservice.dto.ProductResponse;
import com.rahul.orderservice.feign.ProductServiceClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductServiceFallback implements ProductServiceClient {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProductServiceFallback.class);
    @Override
    public ProductResponse getProduct(String productId) {
        logger.info("Product fallback is called");
        return new ProductResponse(productId, "UnavailableDown", BigDecimal.valueOf(0));
    }
}
