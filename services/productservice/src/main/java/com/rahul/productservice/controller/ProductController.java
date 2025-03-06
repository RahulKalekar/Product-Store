package com.rahul.productservice.controller;

import com.rahul.productservice.dto.ProductResponse;
import com.rahul.productservice.entities.Product;
import com.rahul.productservice.service.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/save")
    public ResponseEntity<String> createProduct(@RequestBody @Valid Product product) {
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product created with id: " + savedProduct.getProductId() + " and name: " + savedProduct.getProductName() + " and price:  "+savedProduct.getProductPrice());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        Product product = productService.getProductById(productId);
        ProductResponse response = new ProductResponse(product.getProductId(), product.getProductName(), product.getProductPrice());
        return ResponseEntity.ok(response);
    }
}
