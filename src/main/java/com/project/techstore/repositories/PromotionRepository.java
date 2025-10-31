package com.project.techstore.repositories;

import com.project.techstore.models.Promotion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
    Page<Promotion> findByIsActiveTrue(Pageable pageable);

    boolean existsByCode(String code);
    
    Optional<Promotion> findByCode(String code);

    @Modifying
    @Query("UPDATE Promotion p SET p.isActive = false " +
            "WHERE p.endTime <= :now AND p.isActive = true")
    int deactivateExpiredPromotions(@Param("now")LocalDateTime now);

    List<Promotion> findByStartDateBeforeAndEndTimeAfterAndIsActiveTrue(LocalDateTime now1, LocalDateTime now2);
}
