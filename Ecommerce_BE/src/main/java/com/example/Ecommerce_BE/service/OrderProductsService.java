package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.OrderProductsDTO;
import com.example.Ecommerce_BE.entity.OrderProducts;
import com.example.Ecommerce_BE.entity.Orders;
import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.repository.OrderProductsRepository;
import com.example.Ecommerce_BE.repository.OrdersRepository;
import com.example.Ecommerce_BE.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderProductsService {

    @Autowired
    private OrderProductsRepository orderProductsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductsRepository productsRepository;

    public OrderProductsDTO createOrderProduct(OrderProductsDTO dto) {
        Orders order = ordersRepository.findById(dto.getOrderId()).orElseThrow();
        Products product = productsRepository.findById(dto.getProductId()).orElseThrow();

        OrderProducts orderProduct = new OrderProducts();
        orderProduct.setOrder(order);
        orderProduct.setProduct(product);
        orderProduct.setQuantity(dto.getQuantity());
        orderProduct.setPrice(dto.getPrice());

        orderProductsRepository.save(orderProduct);

        return convertToDTO(orderProduct);
    }

    public List<OrderProductsDTO> getAllOrderProducts() {
        return orderProductsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderProductsDTO getOrderProductById(UUID id) {
        OrderProducts orderProduct = orderProductsRepository.findById(id).orElseThrow();
        return convertToDTO(orderProduct);
    }

    public OrderProductsDTO updateOrderProduct(UUID id, OrderProductsDTO dto) {
        OrderProducts orderProduct = orderProductsRepository.findById(id).orElseThrow();
        orderProduct.setQuantity(dto.getQuantity());
        orderProduct.setPrice(dto.getPrice());

        orderProductsRepository.save(orderProduct);

        return convertToDTO(orderProduct);
    }

    public void deleteOrderProduct(UUID id) {
        orderProductsRepository.deleteById(id);
    }

    private OrderProductsDTO convertToDTO(OrderProducts orderProduct) {
        OrderProductsDTO dto = new OrderProductsDTO();
        dto.setId(orderProduct.getOrderProductId());
        dto.setOrderId(orderProduct.getOrder().getOrderId());
        dto.setProductId(orderProduct.getProduct().getProductId());
        dto.setQuantity(orderProduct.getQuantity());
        dto.setPrice(orderProduct.getPrice());
        return dto;
    }
}
