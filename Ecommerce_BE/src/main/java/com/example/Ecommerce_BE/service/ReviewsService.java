package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.reviews.ReviewCreationRequest;
import com.example.Ecommerce_BE.dto.request.reviews.ReviewUpdateRequest;
import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.entity.Reviews;
import com.example.Ecommerce_BE.entity.Users;
import com.example.Ecommerce_BE.exception.ResourceNotFoundException;
import com.example.Ecommerce_BE.repository.ProductsRepository;
import com.example.Ecommerce_BE.repository.ReviewsRepository;
import com.example.Ecommerce_BE.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewsService {
    private final ReviewsRepository reviewsRepository;
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;

    @Autowired
    public ReviewsService(ReviewsRepository reviewsRepository,
                          UsersRepository usersRepository,
                          ProductsRepository productsRepository) {
        this.reviewsRepository = reviewsRepository;
        this.usersRepository = usersRepository;
        this.productsRepository = productsRepository;
    }

    public Reviews createReview(ReviewCreationRequest request) {
        Reviews reviews = new Reviews();

        Products product = productsRepository.findById(request.getProduct())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Users user = usersRepository.findById(request.getUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        reviews.setProduct(product);
        reviews.setUser(user);
        reviews.setRating(request.getRating());
        reviews.setComment(request.getComment());

        return reviewsRepository.save(reviews);
    }

    public List<Reviews> getAllReviews() {
        return reviewsRepository.findAll();
    }

    public Reviews getReviewById(UUID id) {
        return reviewsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    public Reviews updateReview(UUID id, ReviewUpdateRequest request) {
        Reviews review = reviewsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return reviewsRepository.save(review);
    }

    public void deleteReview(UUID id) {
        reviewsRepository.deleteById(id);
    }
}
