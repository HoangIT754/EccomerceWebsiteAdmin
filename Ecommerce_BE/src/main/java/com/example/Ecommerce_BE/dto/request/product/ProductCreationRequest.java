package com.example.Ecommerce_BE.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductCreationRequest {
//    @NotBlank
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;
//    @NotNull
    private UUID brand;
//    @NotNull
    private List<UUID> subCategories;
    @PositiveOrZero(message = "Price must be larger or equal 0")
    private BigDecimal price;
    @PositiveOrZero(message = "Stock must be larger or equal 0")
    private Integer stock;
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    private String imgURL;
    private Boolean isShow;
}
