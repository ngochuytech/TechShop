package com.project.techstore.repositories;

import com.project.techstore.models.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductModelId(Long id);

    List<Product> findByProductModel_CategoryId(Long categoryId);

    List<Product> findByProductModel_Category_Name(String categoryName);

    List<Product> findByProductModel_Category_NameAndProductModel_Brand_Name(
            String categoryName,
            String brandName
    );

    Page<Product> findByNameContaining(String keyword, Pageable pageable);

    void deleteByProductModelId(Long productModelId);
    
    @Query(value = "SELECT p FROM Product p WHERE p.productModel.category.id = :categoryId " +
            "AND p.id != :productId ORDER BY FUNCTION('RAND')")
    List<Product> findBySimilarCategoryAndDifferentId(
            @Param("categoryId") Long categoryId,
            @Param("productId") String productId,
            @Param("limit") int limit);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +
           "LEFT JOIN FETCH p.productAttributes pa " +
           "WHERE p.id = :id")
    Optional<Product> findProductWithVariantsAndAttributes(@Param("id") String id);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +
           "WHERE p.productModel.id = :modelId " +
           "AND p.id != :currentProductId")
    List<Product> findOtherConfigurations(
        @Param("modelId") Integer modelId,
        @Param("currentProductId") String currentProductId,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false " +
           "AND (:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:productModelId IS NULL OR p.productModel.id = :productModelId)")
    Page<Product> findByFilters(String search, Long productModelId, Pageable pageable);
}
