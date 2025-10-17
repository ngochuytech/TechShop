package com.project.techstore.services.token;

import com.project.techstore.components.JwtTokenProvider;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.exceptions.ExpiredTokenException;
import com.project.techstore.models.Token;
import com.project.techstore.models.User;
import com.project.techstore.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService{

    private final TokenRepository tokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private static final int MAX_TOKENS = 3;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.expiration-refresh-token}")
    private long expirationRefreshToken;

    @Override
    @Transactional
    public Token addToken(User user, String token) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();
        if (tokenCount >= MAX_TOKENS) {
            tokenRepository.delete(userTokens.getFirst());
        }
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .build();
        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        return tokenRepository.save(newToken);
    }

    @Override
    @Transactional
    public Token refreshToken(String refreshToken, User user) throws Exception{
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if(existingToken == null){
            throw new DataNotFoundException("Không tìm thấy RefreshToken");
        }
        if(existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())){
            tokenRepository.delete(existingToken);
            throw new ExpiredTokenException("Refresh token đã hết hạn");
        }
        String token = jwtTokenProvider.generateToken(user);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);
        existingToken.setExpirationDate(expirationDateTime);
        existingToken.setToken(token);
        existingToken.setRefreshToken(UUID.randomUUID().toString());
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        return existingToken;

    }

    @Override
    public Token findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    @Override
    public Token save(Token token) {
        return tokenRepository.save(token);
    }
}
