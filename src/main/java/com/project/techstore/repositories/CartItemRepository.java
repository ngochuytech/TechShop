package com.project.techstore.repositories;

import com.project.techstore.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCartId(String cartId);
    
    Optional<CartItem> findByCartIdAndProductVariantId(String cartId, String productVariantId);
    
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);
    
    void deleteByCartId(String cartId);
}
