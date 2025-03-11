package com.rahul.productservice.service;

import com.rahul.productservice.dto.InventoryRequest;
import com.rahul.productservice.entities.Product;
import com.rahul.productservice.exceptions.ProductNotFoundException;
import com.rahul.productservice.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private KafkaTemplate<String, InventoryRequest> kafkaTemplateCreation;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateDeletion;
    @Override
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productRepo.findAll();
    }
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public Product getProductById(String productId) {
        logger.info("Fetching product with ID: {}", productId);
        return productRepo.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product not found"));
    }

    @Override
    public Product addProduct(Product product) {
        logger.info("Adding product: {}", product);
        Product savedProduct=productRepo.save(product);
        kafkaTemplateCreation.send("product-inventory-creation",(new InventoryRequest(savedProduct.getProductId(), 0)));
        return savedProduct;
    }

    @Override
    public Product updateProduct(Product product) {
        logger.info("Updating product: {}", product);
        if (!productRepo.existsById(product.getProductId())) {
            throw new ProductNotFoundException("Cannot update. Product not found with ID: " + product.getProductId());
        }
        return productRepo.save(product);
    }

    @Override
    public void deleteProduct(String productId) {
        logger.info("Deleting product with ID: {}", productId);
        if (!productRepo.existsById(productId)) {
            throw new ProductNotFoundException("Cannot delete. Product not found with ID: " + productId);
        }
        productRepo.deleteById(productId);
        kafkaTemplateDeletion.send("product-inventory-deletion", productId);
    }
}
