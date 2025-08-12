package com.project.techstore.repositories;

import com.project.techstore.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByUserId(String userId);

    List<Review> findByProductId(String productId);
}
