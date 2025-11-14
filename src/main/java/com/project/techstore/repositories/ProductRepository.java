package com.project.techstore.repositories;

import com.project.techstore.models.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
        List<Product> findByProductModelIdAndIsDeletedFalse(Long id);

        List<Product> findByProductModel_CategoryIdAndIsDeletedFalse(Long categoryId);

        Page<Product> findByProductModel_Category_NameAndIsDeletedFalse(String categoryName, Pageable pageable);

        Page<Product> findByProductModel_Category_NameAndProductModel_Brand_NameAndIsDeletedFalse(
                        String categoryName,
                        String brandName, Pageable pageable);

        Page<Product> findByNameContainingAndIsDeletedFalse(String keyword, Pageable pageable);

        long countByIsDeletedFalse();

        long countByStockLessThan(int threshold);

        @Query("SELECT c.name as category, COUNT(p) as count " +
                        "FROM Product p " +
                        "JOIN p.productModel pm " +
                        "JOIN pm.category c " +
                        "GROUP BY c.name " +
                        "ORDER BY COUNT(p) DESC")
        List<Object[]> countProductsByCategory();

        @Query(value = "SELECT p FROM Product p WHERE p.productModel.category.id = :categoryId " +
                        "AND p.id != :productId ORDER BY FUNCTION('RAND')")
        List<Product> findBySimilarCategoryAndDifferentId(
                        @Param("categoryId") Long categoryId,
                        @Param("productId") String productId,
                        @Param("limit") int limit);

        @Query("SELECT p FROM Product p WHERE p.isDeleted = false " +
                        "AND (:search IS NULL OR :search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
                        +
                        "AND (:productModelId IS NULL OR p.productModel.id = :productModelId)")
        Page<Product> findByFilters(String search, Long productModelId, Pageable pageable);

        @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
        @Modifying
        @Transactional
        int decreaseStock(@Param("productId") String productId, @Param("quantity") int quantity);

        @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :productId")
        @Modifying
        @Transactional
        void increaseStock(@Param("productId") String productId, @Param("quantity") int quantity);
}
