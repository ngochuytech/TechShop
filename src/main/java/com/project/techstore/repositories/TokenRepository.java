package com.project.techstore.repositories;

import com.project.techstore.models.Token;
import com.project.techstore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByUser(User user);
    Token findByRefreshToken(String token);
    Token findByToken(String token);
    void deleteByRefreshToken(String refreshToken);
}
