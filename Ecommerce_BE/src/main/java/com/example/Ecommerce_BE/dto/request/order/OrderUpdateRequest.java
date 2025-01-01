package com.example.Ecommerce_BE.dto.request.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

public class OrderUpdateRequest {
    @NotNull(message = "Order status is required")
    private Integer status;

    private List<ProductOrderInfo> products;

    public @NotNull(message = "Order status is required") Integer getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Order status is required") Integer status) {
        this.status = status;
    }

    public List<ProductOrderInfo> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrderInfo> products) {
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