package com.rahul.productservice.entities;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
public class Product {

    @Getter
    @Id
    private String productId;
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 50, message = "Product name must be between 2 and 50 characters")
    private String productName;
    @NotBlank(message = "Category cannot be blank")
    private String productDescription;
    @NotNull(message = "Price cannot be null")
    @Min(value = 1, message = "Price must be at least 1")
    private BigDecimal productPrice;

    public Product(String productName, String productDescription, BigDecimal productPrice) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
    }
}
