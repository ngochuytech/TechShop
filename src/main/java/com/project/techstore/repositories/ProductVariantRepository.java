package com.project.techstore.repositories;

import com.project.techstore.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List<ProductVariant> findByProductId(String productId);
    
    List<ProductVariant> findByColor(String color);
    
    void deleteByProductId(String productId);
}