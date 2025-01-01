package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.ForgotPasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth-forgot-password")
public class ForgotPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();
        logger.info("Forgot password request received for email: {}", email);

        try {
            String token = getAdminAccessToken();
            logger.info("Admin access token: {}", token);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            String url = keycloakServerUrl + "/admin/realms/" + realm + "/users?email=" + email;

            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

            if (response.getBody() != null && !response.getBody().isEmpty()) {
                Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
                String userId = (String) user.get("id");

                String resetPasswordUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/execute-actions-email?lifespan=3600";

                List<String> actions = Collections.singletonList("UPDATE_PASSWORD");
                HttpEntity<List<String>> resetEntity = new HttpEntity<>(actions, headers);

                restTemplate.put(resetPasswordUrl, resetEntity);

                logger.info("Password reset email sent successfully for user: {}", email);
                return ResponseEntity.ok("Password reset email sent successfully.");
            } else {
                logger.warn("User with email {} not found.", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            logger.error("Error during forgot password process: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    private String getAdminAccessToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                    entity,
                    Map.class
            );

            logger.info("Admin access token response body: {}", response.getBody());
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            logger.error("Failed to get admin access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get admin access token: " + e.getMessage());
        }
    }
}