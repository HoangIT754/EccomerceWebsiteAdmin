package com.example.Ecommerce_BE.dto.request.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public class OrderCreationRequest {
    @NotNull(message = "User ID is required")
    private UUID user;

    private Integer status;

    @NotEmpty(message = "Order must contain at least one product")
    private List<ProductOrderInfo> products;

    public @NotNull(message = "User ID is required") UUID getUser() {
        return user;
    }

    public void setUser(@NotNull(message = "User ID is required") UUID user) {
        this.user = user;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public @NotEmpty(message = "Order must contain at least one product") List<ProductOrderInfo> getProducts() {
        return products;
    }

    public void setProducts(@NotEmpty(message = "Order must contain at least one product") List<ProductOrderInfo> products) {
        this.products = products;
    }

    public static class ProductOrderInfo {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @Positive(message = "Quantity must be greater than 0")
        private Integer quantity;

        public @NotNull(message = "Product ID is required") UUID getProductId() {
            return productId;
        }

        public void setProductId(@NotNull(message = "Product ID is required") UUID productId) {
            this.productId = productId;
        }

        public @Positive(message = "Quantity must be greater than 0") Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(@Positive(message = "Quantity must be greater than 0") Integer quantity) {
            this.quantity = quantity;
        }
    }
}
