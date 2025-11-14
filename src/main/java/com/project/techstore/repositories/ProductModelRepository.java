package com.project.techstore.repositories;

import com.project.techstore.models.ProductModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductModelRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findByCategoryId(Long categoryId);

    List<ProductModel> findByBrandId(Long brandId);
    
    long countByIsDeletedFalse();

    @Query("SELECT pm FROM ProductModel pm WHERE pm.isDeleted = false " +
            "AND (:search IS NULL OR :search = '' OR LOWER(pm.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:categoryId IS NULL OR pm.category.id = :categoryId) " +
            "AND (:brandId IS NULL OR pm.brand.id = :brandId)")
    Page<ProductModel> findByFilters(@Param("search") String search,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            Pageable pageable);

}
