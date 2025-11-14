package com.project.techstore.repositories;

import com.project.techstore.models.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    @Query("SELECT b FROM Banner b WHERE b.active = true ORDER BY b.order ASC")
    List<Banner> findAllActiveBanners();
    
    List<Banner> findAllByOrderByOrderAsc();
}
