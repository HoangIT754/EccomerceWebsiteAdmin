package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.order.OrderCreationRequest;
import com.example.Ecommerce_BE.dto.request.order.OrderUpdateRequest;
import com.example.Ecommerce_BE.entity.Orders;
import com.example.Ecommerce_BE.entity.OrderProducts;
import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.entity.Users;
import com.example.Ecommerce_BE.exception.ResourceNotFoundException;
import com.example.Ecommerce_BE.repository.OrdersRepository;
import com.example.Ecommerce_BE.repository.OrderProductsRepository;
import com.example.Ecommerce_BE.repository.ProductsRepository;
import com.example.Ecommerce_BE.repository.UsersRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final OrderProductsRepository orderProductsRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository, UsersRepository usersRepository,
                         ProductsRepository productsRepository, OrderProductsRepository orderProductsRepository) {
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.productsRepository = productsRepository;
        this.orderProductsRepository = orderProductsRepository;
    }

    public Orders createOrder(@Valid OrderCreationRequest request) {
        Users user = usersRepository.findById(request.getUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUser()));

        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(request.getStatus());

        List<UUID> productIds = request.getProducts().stream()
                .map(OrderCreationRequest.ProductOrderInfo::getProductId)
                .collect(Collectors.toList());

        List<Products> products = productsRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("Some products were not found");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderProducts> orderProductsList = new ArrayList<>();

        for (OrderCreationRequest.ProductOrderInfo productInfo : request.getProducts()) {
            Products product = products.stream()
                    .filter(p -> p.getProductId().equals(productInfo.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderProducts orderProduct = new OrderProducts();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(productInfo.getQuantity());
            BigDecimal productTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(productInfo.getQuantity()));
            orderProduct.setPrice(productTotalPrice);

            totalAmount = totalAmount.add(productTotalPrice);
            orderProductsList.add(orderProduct);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderProducts(orderProductsList);

        return ordersRepository.save(order);
    }

    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Orders getOrderById(UUID orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
    }


    public Orders updateOrder(UUID orderId, @Valid OrderUpdateRequest request) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        order.setStatus(request.getStatus());

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            List<UUID> productIds = request.getProducts().stream()
                    .map(OrderUpdateRequest.ProductOrderInfo::getProductId)
                    .collect(Collectors.toList());

            List<Products> products = productsRepository.findAllById(productIds);

            if (products.size() != productIds.size()) {
                throw new ResourceNotFoundException("Some products were not found");
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderProducts> updatedOrderProducts = new ArrayList<>();

            for (OrderUpdateRequest.ProductOrderInfo productInfo : request.getProducts()) {
                Products product = products.stream()
                        .filter(p -> p.getProductId().equals(productInfo.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                OrderProducts orderProduct = order.getOrderProducts().stream()
                        .filter(op -> op.getProduct().getProductId().equals(product.getProductId()))
                        .findFirst()
                        .orElse(new OrderProducts());

                orderProduct.setOrder(order);
                orderProduct.setProduct(product);
                orderProduct.setQuantity(productInfo.getQuantity());
                BigDecimal productTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(productInfo.getQuantity()));
                orderProduct.setPrice(productTotalPrice);

                totalAmount = totalAmount.add(productTotalPrice);
                updatedOrderProducts.add(orderProduct);
            }

            order.getOrderProducts().clear();
            order.getOrderProducts().addAll(updatedOrderProducts);
            order.setTotalAmount(totalAmount);
        }

        return ordersRepository.save(order);
    }

    public void deleteOrder(UUID orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        ordersRepository.delete(order);
    }
}
