package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.subCategory.SubCategoryCreationRequest;
import com.example.Ecommerce_BE.dto.request.subCategory.SubCategoryUpdateRequest;
import com.example.Ecommerce_BE.entity.SubCategories;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.SubCategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subCategories")
public class SubCategoriesController {

    private final SubCategoriesService subCategoriesService;
    private final AuthService authService;

    @Autowired
    public SubCategoriesController(SubCategoriesService subCategoriesService, AuthService authService) {
        this.subCategoriesService = subCategoriesService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createSubCategory(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @Valid @RequestBody SubCategoryCreationRequest request,
            BindingResult result) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You are not authorized to create subcategories.");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            SubCategories subCategory = subCategoriesService.createSubCategory(request);
            return ResponseEntity.ok(subCategory);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Sub Category name already exists");
        }
    }

    @GetMapping
    public ResponseEntity<List<SubCategories>> getAllSubCategories() {
        List<SubCategories> subCategories = subCategoriesService.getAllSubCategories();
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategories> getSubCategoryById(@PathVariable UUID id) {
        SubCategories subCategory = subCategoriesService.getSubCategoryById(id);
        return ResponseEntity.ok(subCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubCategory(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @PathVariable UUID id,
            @Valid @RequestBody SubCategoryUpdateRequest request,
            BindingResult result) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You are not authorized to update subcategories.");
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            SubCategories updatedSubCategory = subCategoriesService.updateSubCategory(id, request);
            return ResponseEntity.ok(updatedSubCategory);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Sub Category name already exists");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubCategory(
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @PathVariable UUID id) {

        List<String> roles = authService.getUserRoles(authToken);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You are not authorized to delete subcategories.");
        }

        subCategoriesService.deleteSubCategory(id);
        return ResponseEntity.ok("Sub Category deleted successfully");
    }
}
