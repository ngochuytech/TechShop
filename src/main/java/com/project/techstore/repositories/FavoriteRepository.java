package com.project.techstore.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.techstore.models.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    Optional<Favorite> findByUserIdAndProductId(String userId, String productId);

    Page<Favorite> findByUserId(String userId, Pageable pageable);
    
}
