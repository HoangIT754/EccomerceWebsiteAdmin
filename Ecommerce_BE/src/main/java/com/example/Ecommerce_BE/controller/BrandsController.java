package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.brand.BrandCreationRequest;
import com.example.Ecommerce_BE.dto.request.brand.BrandUpdateRequest;
import com.example.Ecommerce_BE.entity.Brands;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.BrandsService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/brands")
public class BrandsController {

    private final BrandsService brandsService;
    private final AuthService authService;

    @Autowired
    public BrandsController(BrandsService brandsService, AuthService authService) {
        this.brandsService = brandsService;
        this.authService = authService;
    }

    private ResponseEntity<?> checkAdminAuthorization(HttpServletRequest request) {
        String token = authService.extractTokenFromCookie(request);
        if (token == null || !authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Invalid or missing token.");
        }
        List<String> roles = authService.getUserRoles(token);
        if (!roles.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Admin privileges required.");
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandCreationRequest request,
                                         BindingResult result, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            Brands brand = brandsService.createBrand(request);
            return ResponseEntity.ok(brand);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Brand name already exists.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllBrands(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(brandsService.getAllBrands());
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<?> getBrandById(@PathVariable UUID brandId, HttpServletRequest httpRequest) {
        Brands brand = brandsService.getBrandById(brandId);
        return ResponseEntity.ok(brand);
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<?> updateBrand(@PathVariable UUID brandId,
                                         @Valid @RequestBody BrandUpdateRequest request,
                                         BindingResult result, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            Brands brand = brandsService.updateBrandById(brandId, request);
            return ResponseEntity.ok(brand);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Brand name already exists.");
        }
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<?> deleteBrandById(@PathVariable UUID brandId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        brandsService.deleteBrandById(brandId);
        return ResponseEntity.ok("Brand deleted successfully.");
    }
}