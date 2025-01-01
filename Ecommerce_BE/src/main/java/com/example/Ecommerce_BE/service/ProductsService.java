package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.product.ProductCreationRequest;
import com.example.Ecommerce_BE.dto.request.product.ProductUpdateRequest;
import com.example.Ecommerce_BE.entity.Brands;
import com.example.Ecommerce_BE.entity.Categories;
import com.example.Ecommerce_BE.entity.Products;
import com.example.Ecommerce_BE.entity.SubCategories;
import com.example.Ecommerce_BE.exception.ResourceNotFoundException;
import com.example.Ecommerce_BE.repository.BrandsRepository;
import com.example.Ecommerce_BE.repository.CategoriesRepository;
import com.example.Ecommerce_BE.repository.ProductsRepository;
import com.example.Ecommerce_BE.repository.SubCategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final BrandsRepository brandsRepository;

    @Autowired
    public ProductsService(ProductsRepository productsRepository,
                           CategoriesRepository categoriesRepository,
                           SubCategoriesRepository subCategoriesRepository,
                           BrandsRepository brandsRepository) {
        this.productsRepository = productsRepository;
        this.categoriesRepository = categoriesRepository;
        this.subCategoriesRepository = subCategoriesRepository;
        this.brandsRepository = brandsRepository;
    }

    private void validateSubCategories(List<SubCategories> subCategories) {
        if (subCategories.isEmpty()) {
            throw new ResourceNotFoundException("One or more SubCategories not found");
        }

        Set<Categories> categoriesSet = subCategories.stream()
                .map(SubCategories::getCategory)
                .collect(Collectors.toSet());
        if (categoriesSet.size() > 1) {
            throw new IllegalArgumentException("All SubCategories must belong to the same Category");
        }
    }

    @Transactional
    public Products createProduct(ProductCreationRequest request) {
        if (productsRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists.");
        }

        Brands brand = brandsRepository.findById(request.getBrand())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrand()));

        List<SubCategories> subCategories = subCategoriesRepository.findAllById(request.getSubCategories());
        validateSubCategories(subCategories);

        Products product = new Products();
        product.setName(request.getName());
        product.setBrand(brand);
        product.setSubCategories(subCategories);
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setImgURL(request.getImgURL());
        product.setIsShow(request.getIsShow());

        return productsRepository.save(product);
    }

    @Transactional
    public Products updateProduct(UUID productId, ProductUpdateRequest request) {
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Brands brand = brandsRepository.findById(request.getBrand())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrand()));

        List<SubCategories> subCategories = subCategoriesRepository.findAllById(request.getSubCategories());
        validateSubCategories(subCategories);

        if (!product.getName().equals(request.getName()) && productsRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product name already exists.");
        }

        product.setName(request.getName());
        product.setBrand(brand);
        product.setSubCategories(subCategories);
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setImgURL(request.getImgURL());
        product.setIsShow(request.getIsShow());

        return productsRepository.save(product);
    }

    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    public Products getProductById(UUID id) {
        return productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Products getProductByName(String name) {
        return productsRepository.findByName(name);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        if (!productsRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        productsRepository.deleteById(productId);
    }

    public boolean isProductDuplicated(String productName) {
        return productsRepository.existsByName(productName);
    }
}
