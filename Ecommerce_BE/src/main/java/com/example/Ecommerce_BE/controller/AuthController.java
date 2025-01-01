package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.LoginRequest;
import com.example.Ecommerce_BE.dto.response.ApiResponse;
import com.example.Ecommerce_BE.dto.response.UserRolesResponse;
import com.example.Ecommerce_BE.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            String token = authService.getToken(request.getUsername(), request.getPassword());

            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
            response.addCookie(cookie);

            ApiResponse apiResponse = new ApiResponse("Login User successfully");
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException ex) {
            logger.error("Login failed: {}", ex.getMessage());
            String message = "";
            HttpStatus status;
            if (ex.getMessage().contains("Account is not fully set up")) {
                status = HttpStatus.FORBIDDEN;
                message = "Account is not fully set up";
            } else if (ex.getMessage().contains("Invalid username or password")) {
                status = HttpStatus.UNAUTHORIZED;
                message = "Invalid username or password";
            } else {
                status = HttpStatus.BAD_REQUEST;
                message = ex.getMessage();
            }
            return ResponseEntity.status(status).body(new ApiResponse(message));
        }
    }

    @PostMapping("/keycloak-login")
    public ResponseEntity<ApiResponse> keycloakLogin(HttpServletResponse response, @RequestBody String keycloakToken) {
        try {
            // Validate and process the Keycloak token if needed
            if (!authService.validateToken(keycloakToken)) {
                throw new RuntimeException("Invalid Keycloak token");
            }

            // Lưu token Keycloak vào cookie
            Cookie cookie = new Cookie("auth_token", keycloakToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
            response.addCookie(cookie);

            return ResponseEntity.ok(new ApiResponse("Login via Keycloak successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Keycloak login failed: " + ex.getMessage()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(new ApiResponse("Logout successfully"));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        boolean isValid = authService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok(new ApiResponse("Token is valid"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid or expired token"));
        }
    }

    @GetMapping("/validate-session")
    public ResponseEntity<Boolean> validateSession(@CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        if (authToken.isEmpty()) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(authService.validateToken(authToken));
    }

    @GetMapping("/user-roles")
    public ResponseEntity<UserRolesResponse> getUserRoles(@CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        try {
            if (authToken.isEmpty()) {
                throw new RuntimeException("Auth token is missing");
            }

            List<String> roles = authService.getUserRoles(authToken);
            return ResponseEntity.ok(new UserRolesResponse("User roles retrieved successfully", roles));
        } catch (Exception e) {
            logger.error("Error retrieving user roles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserRolesResponse("Failed to retrieve user roles: " + e.getMessage(), null));
        }
    }

    @GetMapping("/userId")
    public ResponseEntity<?> getUserIdFromToken(@CookieValue(value = "auth_token", defaultValue = "") String authToken) {
        try {
            if (authToken.isEmpty()) {
                throw new RuntimeException("Auth token is missing");
            }

            String userIdFromToken = authService.getUserIdFromToken(authToken);
            return new ResponseEntity<>(userIdFromToken, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can not retrieve user id");
        }
    }
}