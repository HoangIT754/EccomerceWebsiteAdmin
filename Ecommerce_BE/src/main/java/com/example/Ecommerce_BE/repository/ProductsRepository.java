package com.example.Ecommerce_BE.repository;

import com.example.Ecommerce_BE.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductsRepository extends JpaRepository<Products, UUID> {
    boolean existsByName(String name);
    Products findByName(String name);
}
