package com.project.techstore.repositories;

import com.project.techstore.models.ProductVariant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List<ProductVariant> findByProductIdAndIsDeletedFalse(String productId);

    Page<ProductVariant> findByProductIdAndIsDeletedFalse(String productId, Pageable pageable);
    
    List<ProductVariant> findByColorAndIsDeletedFalse(String color);

    long countByIsDeletedFalse();

    @Query("UPDATE ProductVariant pv SET pv.stock = pv.stock - :quantity WHERE pv.id = :variantId and pv.stock >= :quantity")
    @Modifying
    @Transactional
    int decreaseStock(@Param("variantId") String variantId, @Param("quantity") int quantity);

    @Query("UPDATE ProductVariant pv SET pv.stock = pv.stock + :quantity WHERE pv.id = :variantId")
    @Modifying
    @Transactional
    void increaseStock(@Param("variantId") String variantId, @Param("quantity") int quantity);
}