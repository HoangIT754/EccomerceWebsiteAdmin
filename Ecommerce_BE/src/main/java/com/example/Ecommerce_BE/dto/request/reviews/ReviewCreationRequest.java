package com.example.Ecommerce_BE.dto.request.reviews;

import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.entity.Users;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewCreationRequest {
    @NotNull
    private UUID product;
    @NotNull
    private UUID user;
    @PositiveOrZero(message = "Rating must be lager or equal 0")
    private Integer rating;
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    public UUID getProduct() {
        return product;
    }

    public void setProduct(UUID product) {
        this.product = product;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
