package com.project.techstore.repositories;

import com.project.techstore.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductModelId(Long id);

    @Query("SELECT p FROM Product p JOIN p.productModel pm " +
            "where pm.category.id = :categoryId")
    List<Product> findByCategory(@Param("categoryId") Long categoryId);

    List<Product> findByProductModel_Category_Name(String categoryName);

    List<Product> findByProductModel_Category_NameAndProductModel_Brand_Name(
            String categoryName,
            String brandName
    );

    void deleteByProductModelId(Long productModelId);
}
