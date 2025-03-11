package com.rahul.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.productservice.entities.Product;
import com.rahul.productservice.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceImpl productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("1");
        product.setProductName("Test Product");
        product.setProductPrice(BigDecimal.valueOf(100.0));
        product.setProductDescription("Sample description"); // Ensure all required fields are set
    }

    @Test
    void getAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(products.size()))
                .andExpect(jsonPath("$[0].productId").value(product.getProductId()))
                .andExpect(jsonPath("$[0].productName").value(product.getProductName()))
                .andExpect(jsonPath("$[0].productPrice").value(product.getProductPrice().doubleValue()));
    }

    @Test
    void createProduct() throws Exception {
        when(productService.addProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product created with id: " + product.getProductId() + " and name: " + product.getProductName() + " and price:  " + product.getProductPrice()));
    }

    @Test
    void getProduct() throws Exception {
        when(productService.getProductById("1")).thenReturn(product);

        mockMvc.perform(get("/products/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product.getProductId()))
                .andExpect(jsonPath("$.name").value(product.getProductName()))  // Updated field name
                .andExpect(jsonPath("$.price").value(product.getProductPrice().doubleValue()));
    }
}