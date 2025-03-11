package com.rahul.orderservice.feign;

import com.rahul.orderservice.dto.ProductResponse;
import com.rahul.orderservice.fallback.ProductServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service",url="http://localhost:8091",fallback = ProductServiceFallback.class)
public interface ProductServiceClient {
    @GetMapping("/api/products/id/{productId}")
    ProductResponse getProduct(@PathVariable String productId);
}
