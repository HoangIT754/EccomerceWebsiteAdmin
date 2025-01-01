package com.example.Ecommerce_BE.repository;

import com.example.Ecommerce_BE.entity.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductsRepository extends JpaRepository<OrderProducts, UUID> {

}
