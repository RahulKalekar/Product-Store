package com.rahul.productservice.service;

import com.rahul.productservice.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    public List<Product> getAllProducts();
    public Product getProductById(String productId);
    public Product addProduct(Product product);
    public Product updateProduct(Product product);
    public void deleteProduct(String productId);
}
