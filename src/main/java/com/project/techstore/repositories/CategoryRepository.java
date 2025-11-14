package com.project.techstore.repositories;

import com.project.techstore.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.id IN :ids")
    Set<Category> findByIds(@Param("ids") Set<Long> ids);

    boolean existsByName(String name);
}
