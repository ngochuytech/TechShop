package com.project.techstore.repositories;

import com.project.techstore.models.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByUserId(String userId);

    List<Review> findByProductId(String productId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> countReviewByStar(@Param("productId") String productId);

    Page<Review> findByProductId(String productId, Pageable pageable);

    Page<Review> findByProductIdAndRating(String productId, int rating, Pageable pageable);
}
