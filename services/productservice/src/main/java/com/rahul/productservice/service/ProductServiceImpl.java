package com.rahul.productservice.service;

import com.rahul.productservice.entities.Product;
import com.rahul.productservice.exceptions.ProductNotFoundException;
import com.rahul.productservice.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepo productRepo;
    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(String productId) {
        return productRepo.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));
    }

    @Override
    public Product addProduct(Product product) {
        if (!productRepo.existsById(product.getProductId())) {
            throw new ProductNotFoundException("Cannot update. Product not found with ID: " + product.getProductId());
        }
        return productRepo.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepo.save(product);
    }

    @Override
    public void deleteProduct(String productId) {
        if (!productRepo.existsById(productId)) {
            throw new ProductNotFoundException("Cannot delete. Product not found with ID: " + productId);
        }
        productRepo.deleteById(productId);
    }
}
