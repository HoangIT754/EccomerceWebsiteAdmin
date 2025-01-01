package com.example.Ecommerce_BE.controller;

import com.example.Ecommerce_BE.dto.request.order.OrderCreationRequest;
import com.example.Ecommerce_BE.dto.request.order.OrderUpdateRequest;
import com.example.Ecommerce_BE.entity.Orders;
import com.example.Ecommerce_BE.service.AuthService;
import com.example.Ecommerce_BE.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Validated
public class OrdersController {

    private final OrdersService ordersService;
    private final AuthService authService;

    @Autowired
    public OrdersController(OrdersService ordersService, AuthService authService) {
        this.ordersService = ordersService;
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
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderCreationRequest request, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        Orders order = ordersService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Orders>> getAllOrders(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ordersService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable UUID orderId, HttpServletRequest httpRequest) {
        Orders order = ordersService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID orderId,
                                         @Valid @RequestBody OrderUpdateRequest request,
                                         HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        Orders updatedOrder = ordersService.updateOrder(orderId, request);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId, HttpServletRequest httpRequest) {
        ResponseEntity<?> authResponse = checkAdminAuthorization(httpRequest);
        if (authResponse != null) return authResponse;

        ordersService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully.");
    }
}