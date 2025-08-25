package com.project.techstore.repositories;

import com.project.techstore.models.User;
import com.project.techstore.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(User user);
}
