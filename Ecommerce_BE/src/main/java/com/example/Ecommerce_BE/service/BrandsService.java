package com.example.Ecommerce_BE.service;

import com.example.Ecommerce_BE.dto.request.brand.BrandCreationRequest;
import com.example.Ecommerce_BE.dto.request.brand.BrandUpdateRequest;
import com.example.Ecommerce_BE.entity.Brands;
import com.example.Ecommerce_BE.exception.ResourceNotFoundException;
import com.example.Ecommerce_BE.repository.BrandsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BrandsService {
    private final BrandsRepository brandsRepository;

    @Autowired
    public BrandsService(BrandsRepository brandsRepository) {
        this.brandsRepository = brandsRepository;
    }

    public Brands createBrand(BrandCreationRequest request) {

        if (brandsRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand name already exists.");
        }

        Brands brand = new Brands();

        brand.setName(request.getName());
        brand.setDescription(request.getDescription());

        return brandsRepository.save(brand);
    }

    public List<Brands> getAllBrands() {
        return brandsRepository.findAll();
    }

    public Brands getBrandById(UUID id) {
        return brandsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found!"));
    }

    public Brands updateBrandById(UUID id, BrandUpdateRequest request) {
        Brands brand = brandsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found!"));

        if (!brand.getName().equals(request.getName()) && brandsRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Brand name already exists.");
        }


        brand.setName(request.getName());
        brand.setDescription(request.getDescription());

        return brandsRepository.save(brand);
    }

    public void deleteBrandById(UUID id) {
        brandsRepository.deleteById(id);
    }
}
