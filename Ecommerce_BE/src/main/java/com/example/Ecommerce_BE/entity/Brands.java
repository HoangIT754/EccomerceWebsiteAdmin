package com.example.Ecommerce_BE.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Brands {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID brand_id;
    private String name;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    @PrePersist
    public void prePersist(){
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
        if (updated_at == null) {
            updated_at = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate(){
        updated_at = LocalDateTime.now();
    }

    public UUID getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(UUID brand_id) {
        this.brand_id = brand_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
