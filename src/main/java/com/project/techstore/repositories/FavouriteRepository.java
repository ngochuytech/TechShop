package com.project.techstore.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.techstore.models.Favourite;

public interface FavouriteRepository extends JpaRepository<Favourite, String> {
    Optional<Favourite> findByUserIdAndProductId(String userId, String productId);

    Page<Favourite> findByUserId(String userId, Pageable pageable);
    
}
