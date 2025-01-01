package com.example.Ecommerce_BE.dto.request.brand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BrandUpdateRequest {
    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must be less than 100 characters")
    private String name;
    @Size(max = 500, message = "Discription must be less than 500 characters")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
