package com.project.techstore.repositories;

import com.project.techstore.models.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, String> {
    void deleteByProductId(String productId);
}
