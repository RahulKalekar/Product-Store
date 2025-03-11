package com.rahul.productservice.service;

import com.rahul.productservice.dto.InventoryRequest;
import com.rahul.productservice.entities.Product;
import com.rahul.productservice.exceptions.ProductNotFoundException;
import com.rahul.productservice.repo.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// src/test/java/com/rahul/productservice/service/ProductServiceImplTest.java
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepo productRepo;
    
    @Mock
    private KafkaTemplate<String, InventoryRequest> kafkaTemplateCreation;
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplateDeletion;
    
    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", BigDecimal.valueOf(99.99));
        testProduct.setProductId("1");
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(productRepo.findAll()).thenReturn(List.of(testProduct));
        
        List<Product> result = productService.getAllProducts();
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testProduct);
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepo.findById("1")).thenReturn(Optional.of(testProduct));
        
        Product result = productService.getProductById("1");
        
        assertThat(result).isEqualTo(testProduct);
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        when(productRepo.findById("1")).thenReturn(Optional.empty());
        
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById("1"));
    }

    @Test
    void addProduct_ShouldSaveAndReturnProduct() {
        when(productRepo.save(any(Product.class))).thenReturn(testProduct);
        
        Product result = productService.addProduct(testProduct);
        
        assertThat(result).isEqualTo(testProduct);
        verify(kafkaTemplateCreation).send(eq("product-inventory-creation"), any(InventoryRequest.class));
    }
}