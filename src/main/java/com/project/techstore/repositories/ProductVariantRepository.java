package com.project.techstore.repositories;

import com.project.techstore.models.ProductVariant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List<ProductVariant> findByProductIdAndIsDeletedFalse(String productId);

    Page<ProductVariant> findByProductIdAndIsDeletedFalse(String productId, Pageable pageable);
    
    List<ProductVariant> findByColorAndIsDeletedFalse(String color);
}