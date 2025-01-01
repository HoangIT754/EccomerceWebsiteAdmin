package com.example.Ecommerce_BE.repository;

import com.example.Ecommerce_BE.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, UUID> {
    boolean existsByName(String name);
}
