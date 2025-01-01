package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.subCategory.SubCategoryCreationRequest;
import com.example.Ecommerce_BE.dto.request.subCategory.SubCategoryUpdateRequest;
import com.example.Ecommerce_BE.entity.Categories;
import com.example.Ecommerce_BE.entity.SubCategories;
import com.example.Ecommerce_BE.exception.ResourceNotFoundException;
import com.example.Ecommerce_BE.repository.CategoriesRepository;
import com.example.Ecommerce_BE.repository.SubCategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubCategoriesService {

    private final SubCategoriesRepository subCategoriesRepository;
    private final CategoriesRepository categoriesRepository;

    @Autowired
    public SubCategoriesService(SubCategoriesRepository subCategoriesRepository,
                                CategoriesRepository categoriesRepository) {
        this.subCategoriesRepository = subCategoriesRepository;
        this.categoriesRepository = categoriesRepository;
    }

    public SubCategories createSubCategory(SubCategoryCreationRequest request) {

        if (subCategoriesRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Sub category name already exists");
        }

        Categories category = categoriesRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategory_id()));

        SubCategories subCategory = new SubCategories();
        subCategory.setName(request.getName());
        subCategory.setDescription(request.getDescription());
        subCategory.setCategory(category);

        return subCategoriesRepository.save(subCategory);
    }

    public List<SubCategories> getAllSubCategories() {
        return subCategoriesRepository.findAll();
    }

    public SubCategories getSubCategoryById(UUID subCategoryId) {
        return subCategoriesRepository.findById(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id: " + subCategoryId));
    }

    public SubCategories updateSubCategory(UUID subCategoryId, SubCategoryUpdateRequest request) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id: " + subCategoryId));

        Categories category = categoriesRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategory_id()));

        if (!subCategory.getName().equals(request.getName()) && subCategoriesRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Sub category name already exists");
        }

        subCategory.setName(request.getName());
        subCategory.setDescription(request.getDescription());
        subCategory.setCategory(category);

        return subCategoriesRepository.save(subCategory);
    }

    public void deleteSubCategory(UUID subCategoryId) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id: " + subCategoryId));
        subCategoriesRepository.delete(subCategory);
    }
}
