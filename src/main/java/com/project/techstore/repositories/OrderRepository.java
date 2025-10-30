package com.project.techstore.repositories;

import com.project.techstore.models.Order;
import com.project.techstore.models.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByStatus(String status);

    List<Order> findByUserId(String userId);
    
    @EntityGraph(attributePaths = {"orderItems"})
    Optional<Order> findById(String id);
    /**
     * Đếm số đơn hàng của user theo status
     */
    long countByUserAndStatus(User user, String status);
    
    /**
     * Đếm số đơn hàng của user trừ status bị loại trừ
     */
    long countByUserAndStatusNot(User user, String status);
}
