package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.reviews.ReviewCreationRequest;
import com.example.Ecommerce_BE.dto.request.reviews.ReviewUpdateRequest;
import com.example.Ecommerce_BE.entity.Reviews;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.ReviewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {
    @Autowired
    private ReviewsService reviewsService;

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(ReviewsController.class);


    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewCreationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        try {
            Reviews reviews = reviewsService.createReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reviews);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }

    @GetMapping
    List<Reviews> getAllReviews() {
        return reviewsService.getAllReviews();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Reviews> getReviewById(@PathVariable UUID reviewId) {
        Reviews reviews = reviewsService.getReviewById(reviewId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable UUID reviewId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @Valid @RequestBody ReviewUpdateRequest request,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        String userIdFromToken = authService.getUserIdFromToken(authToken);
        logger.info("userIdFromToken: " + userIdFromToken);
        Reviews review = reviewsService.getReviewById(reviewId);
        logger.info("review user ID: " + review.getUser().getId());
        if (!review.getUser().getId().toString().equals(userIdFromToken)) {
            logger.info("Is own review: {}", review.getUser().getId().toString().equals(userIdFromToken));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to update this review.");
        }

        Reviews updatedReview = reviewsService.updateReview(reviewId, request);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable UUID reviewId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken) {

        String userIdFromToken = authService.getUserIdFromToken(authToken);
        List<String> roles = authService.getUserRoles(authToken);

        Reviews review = reviewsService.getReviewById(reviewId);
        if (!review.getUser().getId().toString().equals(userIdFromToken) || roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to delete this review.");
        }

        reviewsService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully.");
    }
}
