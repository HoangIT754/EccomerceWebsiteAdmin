package com.example.Ecommerce_BE.dto.request.reviews;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ReviewUpdateRequest {
    @PositiveOrZero(message = "Rating must be lager or equal 0")
    private Integer rating;
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

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
