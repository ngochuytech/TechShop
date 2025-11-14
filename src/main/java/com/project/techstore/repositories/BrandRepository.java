package com.project.techstore.repositories;

import com.project.techstore.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT b FROM Brand b WHERE b.id IN :ids")
    Set<Brand> findByIds(@Param("ids") Set<Long> ids);

    boolean existsByName(String name);

    @Query("SELECT DISTINCT b FROM Brand b JOIN ProductModel pm ON pm.brand.id = b.id WHERE pm.category.name = :categoryName AND pm.isDeleted = false")
    List<Brand> findByCategoryName(@Param("categoryName") String categoryName);
}
