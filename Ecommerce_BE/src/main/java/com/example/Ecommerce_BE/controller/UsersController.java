package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.user.UserCreationRequest;
import com.example.Ecommerce_BE.dto.request.user.UserUpdateRequest;
import com.example.Ecommerce_BE.entity.Users;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @PostMapping("/register")
    public Users registerUser(@Valid @RequestBody UserCreationRequest request) {
        return usersService.createUser(request);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        List<String> roles = authService.getUserRoles(authToken);
        if (roles.contains("admin")) {
            return ResponseEntity.ok(usersService.getAllUsers());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to access this resource.");
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(
            @PathVariable UUID userId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken) {

        String userIdFromToken = authService.getUserIdFromToken(authToken);
        List<String> roles = authService.getUserRoles(authToken);

        if (userIdFromToken.equals(userId.toString()) || roles.contains("admin")) {
            return ResponseEntity.ok(usersService.getUserById(userId));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to access this resource.");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserById(
            @PathVariable UUID userId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken,
            @Valid @RequestBody UserUpdateRequest request) {

        String userIdFromToken = authService.getUserIdFromToken(authToken);
        List<String> roles = authService.getUserRoles(authToken);

        if (userIdFromToken.equals(userId.toString()) || roles.contains("admin")) {
            return ResponseEntity.ok(usersService.updateUserById(userId, request));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to update this user.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(
            @PathVariable UUID userId,
            @CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        List<String> roles = authService.getUserRoles(authToken);
        String userIdFromToken = authService.getUserIdFromToken(authToken);

        if (roles.contains("admin") || userIdFromToken.equals(userId.toString())) {
            usersService.deleteUserById(userId);
            return ResponseEntity.ok("User deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You are not authorized to delete users.");
    }

    @GetMapping("/update-profile")
    public String updateProfile(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        boolean isUpdated = usersService.updateProfileByUsername(username, firstName, lastName, email);

        if (isUpdated) {
            return "Profile updated successfully!";
        } else {
            return "Error updating profile. User not found.";
        }
    }
}
