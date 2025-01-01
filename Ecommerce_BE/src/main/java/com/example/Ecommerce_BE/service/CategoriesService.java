package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.category.CategoryCreationRequest;
import com.example.Ecommerce_BE.dto.request.category.CategoryUpdateRequest;
import com.example.Ecommerce_BE.entity.Categories;
import com.example.Ecommerce_BE.exception.ResourceNotFoundException;
import com.example.Ecommerce_BE.repository.BrandsRepository;
import com.example.Ecommerce_BE.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final BrandsRepository brandsRepository;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository, BrandsRepository brandsRepository) {
        this.categoriesRepository = categoriesRepository;
        this.brandsRepository = brandsRepository;
    }

    public Categories createCategory(CategoryCreationRequest request) {
        if(categoriesRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Categories category = new Categories();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return categoriesRepository.save(category);
    }

    public Categories updateCategory(UUID categoryId, CategoryUpdateRequest request) {
        Categories category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if(!category.getName().equals(request.getName()) && brandsRepository.existsByName(category.getName())   ) {
            throw new IllegalArgumentException("Category name already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return categoriesRepository.save(category);
    }

    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public Categories getCategoryById(UUID id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    public void deleteCategory(UUID categoryId) {
        categoriesRepository.deleteById(categoryId);
    }
}
