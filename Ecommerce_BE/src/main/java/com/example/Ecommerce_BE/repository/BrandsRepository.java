package com.example.Ecommerce_BE.repository;

import com.example.Ecommerce_BE.entity.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrandsRepository extends JpaRepository<Brands, UUID> {
    boolean existsByName(String name);
}
