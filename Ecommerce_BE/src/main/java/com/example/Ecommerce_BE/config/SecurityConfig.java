package com.example.Ecommerce_BE.config;

import com.example.Ecommerce_BE.config.JWT.JwtConverter;
import com.example.Ecommerce_BE.config.JWT.JwtCookieFilter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot API Documentation")
                        .version("1.0.0")
                        .description("Tài liệu API của dự án Spring Boot"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtConverter jwtConverter, JwtCookieFilter jwtCookieFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Public API
                        .requestMatchers("/auth/**","/products/**", "/categories/**",
                                "/subCategories/**", "/brands/**", "/report/**",
                                "/orders/**", "/users/**", "/reviews/**",
                                "/camunda/**", "/workflow/**","/auth-forgot-password/**"
                        ).permitAll()
//                        .requestMatchers("/auth/**","/auth-forgot-password/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/products/**", "/reviews/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
//
//                        // User-specific API
//                        .requestMatchers(HttpMethod.GET, "/orders/{orderId}", "/users/{userId}").hasAuthority("ROLE_user")
//                        .requestMatchers(HttpMethod.POST, "/orders/**", "/reviews/**").hasAuthority("ROLE_user")
//                        .requestMatchers(HttpMethod.PUT, "/users/{userId}", "/reviews/{reviewId}", "/orders/**").hasAuthority("ROLE_user")
//                        .requestMatchers(HttpMethod.DELETE, "/reviews/{reviewId}", "/users/{userId}").hasAuthority("ROLE_user")
//
//                        // Admin-only API
//                        .requestMatchers("/products/**", "/categories/**", "/subCategories/**", "/brands/**", "/report/**").hasAuthority("ROLE_admin")
//                        .requestMatchers("/orders/**", "/users/**", "/reviews/**").hasAuthority("ROLE_admin")

                        // Default rule
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "http://localhost:9091/realms/Ecommerce_Website/protocol/openid-connect/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}