package com.project.techstore.repositories;

import com.project.techstore.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductModelRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findByCategoryId(Long categoryId);

    List<ProductModel> findByBrandId(Long brandId);

}
