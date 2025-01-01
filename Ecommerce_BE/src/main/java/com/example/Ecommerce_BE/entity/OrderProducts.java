package com.example.Ecommerce_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderProductId;

    @ManyToOne
    @JoinColumn(name = "orderId")
    @JsonBackReference
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "productId")
    @JsonManagedReference
    private Products product;

    private Integer quantity;
    private BigDecimal price;

    // Getters và setters
    public UUID getOrderProductId() {
        return orderProductId;
    }

    public void setOrderProductId(UUID orderProductId) {
        this.orderProductId = orderProductId;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
