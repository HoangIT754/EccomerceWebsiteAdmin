package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.OrderProductsDTO;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.OrderProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-products")
public class OrderProductsController {

    private final OrderProductsService orderProductsService;
    private final AuthService authService;

    @Autowired
    public OrderProductsController(OrderProductsService orderProductsService, AuthService authService) {
        this.orderProductsService = orderProductsService;
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
    public ResponseEntity<?> createOrderProduct(@RequestBody OrderProductsDTO dto, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        OrderProductsDTO createdOrderProduct = orderProductsService.createOrderProduct(dto);
        return ResponseEntity.ok(createdOrderProduct);
    }

    @GetMapping
    public ResponseEntity<List<OrderProductsDTO>> getAllOrderProducts(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(orderProductsService.getAllOrderProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderProductById(@PathVariable UUID id, HttpServletRequest httpRequest) {
        OrderProductsDTO orderProduct = orderProductsService.getOrderProductById(id);
        return ResponseEntity.ok(orderProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderProduct(@PathVariable UUID id,
                                                @RequestBody OrderProductsDTO dto,
                                                HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        OrderProductsDTO updatedOrderProduct = orderProductsService.updateOrderProduct(id, dto);
        return ResponseEntity.ok(updatedOrderProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderProduct(@PathVariable UUID id, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        orderProductsService.deleteOrderProduct(id);
        return ResponseEntity.ok("Order product deleted successfully.");
    }
}
