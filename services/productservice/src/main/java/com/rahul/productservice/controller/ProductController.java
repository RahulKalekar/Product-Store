package com.rahul.productservice.controller;

import com.rahul.productservice.dto.ProductResponse;
import com.rahul.productservice.entities.Product;
import com.rahul.productservice.service.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")

public class ProductController {
private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductServiceImpl productService;

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/save")
    public ResponseEntity<String> createProduct(@RequestBody @Valid Product product) {
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product created with id: " + savedProduct.getProductId() + " and name: " + savedProduct.getProductName() + " and price:  "+savedProduct.getProductPrice());
    }

    @GetMapping("/id/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        ProductResponse response = new ProductResponse(product.getProductId(), product.getProductName(), product.getProductPrice());
        return ResponseEntity.ok(response);
    }
}
