package com.example.Ecommerce_BE.repository;

import com.example.Ecommerce_BE.entity.SubCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubCategoriesRepository extends JpaRepository<SubCategories, UUID> {
    boolean existsByName(String name);
}
