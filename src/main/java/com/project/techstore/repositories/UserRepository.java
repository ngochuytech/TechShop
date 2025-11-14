package com.project.techstore.repositories;

import com.project.techstore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    long countByRole_Name(String role);

    // Đếm khách hàng mới trong khoảng thời gian
    long countByRole_NameAndCreatedAtBetween(
            String role,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
