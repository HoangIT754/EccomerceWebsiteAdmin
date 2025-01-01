package com.example.Ecommerce_BE.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final JwtDecoder jwtDecoder;


    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final WebClient webClient;

    public AuthService(WebClient webClient, @Qualifier("jwtDecoder") JwtDecoder jwtDecoder) {
        this.webClient = webClient;
        this.jwtDecoder = jwtDecoder;
    }

    public String getToken(String username, String password) {
        String tokenUrl = keycloakAuthUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        try {
            logger.info("Attempting to retrieve token from URL: {}", tokenUrl);
            Mono<Map<String, Object>> response = webClient.post()
                    .uri(tokenUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("username", username)
                            .with("password", password)
                            .with("grant_type", "password"))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> responseBody = response.block();
            logger.info("Response body received: {}", responseBody);

            if (responseBody == null) {
                logger.error("Response body is null.");
                throw new RuntimeException("Unexpected null response from Keycloak");
            }

            if (responseBody.containsKey("error")) {
                String error = (String) responseBody.get("error");
                String errorDescription = (String) responseBody.get("error_description");

                logger.error("Error from Keycloak: {} - {}", error, errorDescription);

                if ("invalid_grant".equals(error) && "Account is not fully set up".equals(errorDescription)) {
                    throw new RuntimeException("Account is not fully set up. Please complete account setup.");
                } else {
                    throw new RuntimeException("Authentication error: " + errorDescription);
                }
            } else if (responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            }

            logger.error("Access token not found in response.");
            throw new RuntimeException("Access token not found");
        } catch (WebClientResponseException e) {
            logger.error("HTTP error while fetching token: Status {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Authentication error: HTTP " + e.getRawStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Authentication error: " + e.getMessage(), e);
        }
    }



    public boolean validateToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getExpiresAt() != null && jwt.getExpiresAt().isAfter(java.time.Instant.now());
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public List<String> getUserRoles(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            // Lấy roles từ "realm_access"
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            List<String> roles = (List<String>) realmAccess.get("roles");

            if (roles == null || roles.isEmpty()) {
                throw new RuntimeException("No roles found for the user");
            }

            return roles;
        } catch (JwtException e) {
            logger.error("Invalid JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error extracting roles from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting roles from token: " + e.getMessage());
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String subject = jwt.getClaim("sub");
            if (subject != null) {
                String[] parts = subject.split(":");
                if (parts.length > 0) {
                    logger.info(parts[1]);
                    return parts[parts.length - 1];
                }
            }
            throw new RuntimeException("Invalid token format: 'sub' claim is missing or invalid");
        } catch (JwtException e) {
            logger.error("Invalid JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting user ID from token: " + e.getMessage());
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getClaim("preferred_username");

            if (username != null && !username.isEmpty()) {
                return username;
            }

            throw new RuntimeException("Invalid token format: 'preferred_username' claim is missing or invalid");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error extracting username from token: " + e.getMessage());
        }
    }

}
