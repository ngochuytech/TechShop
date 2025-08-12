package com.project.techstore.repositories;

import com.project.techstore.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByStatus(String status);

    List<Order> findByUserId(String userId);
}
