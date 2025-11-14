package com.project.techstore.repositories;

import com.project.techstore.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    
    boolean existsByUserId(String userId);
    
    @Query("DELETE FROM CartItem ci WHERE ci.cart.user.id = :userId AND " +
            "((CASE WHEN :variantId IS NOT NULL THEN ci.productVariant.id = :variantId ELSE FALSE END) OR " +
            "(CASE WHEN :variantId IS NULL AND :productId IS NOT NULL THEN ci.product.id = :productId AND ci.productVariant IS NULL ELSE FALSE END))")
    @Modifying
    @Transactional
    void deleteCartItemByUserAndProduct(@Param("userId") String userId, 
                                        @Param("productId") String productId,
                                        @Param("variantId") String variantId);
}
