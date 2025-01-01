package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.product.ProductCreationRequest;
import com.example.Ecommerce_BE.dto.request.product.ProductUpdateRequest;
import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.ProductsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsService productsService;
    private final View error;
    private final AuthService authService;


    @Autowired
    public ProductsController(ProductsService productsService, View error, AuthService authService) {
        this.productsService = productsService;
        this.error = error;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @Valid @RequestBody ProductCreationRequest request,
            BindingResult result) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to create products.");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        try {
            Products product = productsService.createProduct(request);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Product name already exists");
        }
    }

    @GetMapping
    public List<Products> getAllProducts() {
        return productsService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Products> getProductById(@PathVariable UUID productId) {
        Products product = productsService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID productId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @Valid @RequestBody ProductUpdateRequest request,
            BindingResult result) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to update products.");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            Products updatedProduct = productsService.updateProduct(productId, request);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Product name already exists");
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable UUID productId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to delete products.");
        }

        productsService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
