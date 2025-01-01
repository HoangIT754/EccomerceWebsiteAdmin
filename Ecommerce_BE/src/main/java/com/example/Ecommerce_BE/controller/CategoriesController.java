package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.category.CategoryCreationRequest;
import com.example.Ecommerce_BE.dto.request.category.CategoryUpdateRequest;
import com.example.Ecommerce_BE.entity.Categories;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.CategoriesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final AuthService authService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService, AuthService authService) {
        this.categoriesService = categoriesService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @Valid @RequestBody CategoryCreationRequest request,
            BindingResult result) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You are not authorized to create categories.");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            Categories category = categoriesService.createCategory(request);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body("Category name already exists");
        }
    }


    @GetMapping
    public List<Categories> getAllCategories() {
        return categoriesService.getAllCategories();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Categories> getCategoryById(@PathVariable UUID categoryId) {
        Categories category = categoriesService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryUpdateRequest request,
            BindingResult result) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You are not authorized to update categories.");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            Categories updatedCategory = categoriesService.updateCategory(categoryId, request);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Category name already exists");
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @PathVariable UUID categoryId) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You are not authorized to delete categories.");
        }

        categoriesService.deleteCategory(categoryId);
        return ResponseEntity.ok("Category deleted successfully");
    }
}
