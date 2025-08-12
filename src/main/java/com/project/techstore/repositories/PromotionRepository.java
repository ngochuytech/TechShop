package com.project.techstore.repositories;

import com.project.techstore.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
    List<Promotion> findByCategoriesId(Long categoryId);

    List<Promotion> findByBrandsId(Long brandId);

    List<Promotion> findByCategoriesIdAndIsActiveTrue(Long categoryId);

    List<Promotion> findByBrandsIdAndIsActiveTrue(Long brandId);

    @Modifying
    @Query("UPDATE Promotion p SET p.isActive = false " +
            "WHERE p.endTime <= :now AND p.isActive = true")
    int deactivateExpiredPromotions(@Param("now")LocalDateTime now);
}
