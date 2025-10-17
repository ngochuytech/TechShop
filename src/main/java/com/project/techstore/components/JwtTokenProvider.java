package com.project.techstore.components;

import com.project.techstore.exceptions.ExpiredTokenException;
import com.project.techstore.exceptions.InvalidParamException;
import com.project.techstore.models.Token;
import com.project.techstore.models.User;
import com.project.techstore.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationDate;

    private final TokenRepository tokenRepository;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    public String generateToken(User user){
        String username = user.getEmail();

        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    private SecretKey key(){
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    // get username from JWT token
    public String getUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token đã hết hạn");
        } catch (JwtException e) {
            throw new InvalidParamException("Token không hợp lệ: " + e.getMessage());
        }
    }

    // validate JWT token
    public boolean validateToken(String token, User user) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            // Subject là email

            // Kiểm tra token tồn tại trong DB ko ?
            Token existingToken = tokenRepository.findByToken(token);
            if(existingToken == null) {
                throw new InvalidParamException("Token không tồn tại trong hệ thống");
            }
            if(existingToken.isRevoked()) {
                throw new InvalidParamException("Token đã bị thu hồi");
            }
            if(!user.getIsActive()) {
                throw new InvalidParamException("Tài khoản đã bị vô hiệu hóa");
            }
            if(isTokenExpired(token)) {
                throw new ExpiredTokenException("Token đã hết hạn");
            }
            return subject.equals(user.getUsername());
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token đã hết hạn");
        } catch (JwtException e) {
            throw new InvalidParamException("Token không hợp lệ: " + e.getMessage());
        }
    }
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()  // Khởi tạo JwtParserBuilder
                    .verifyWith(key())  // Sử dụng verifyWith() để thiết lập signing key
                    .build()  // Xây dựng JwtParser
                    .parseSignedClaims(token)  // Phân tích token đã ký
                    .getPayload();  // Lấy phần body của JWT, chứa claims
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token đã hết hạn");
        } catch (JwtException e) {
            throw new InvalidParamException("Token không hợp lệ: " + e.getMessage());
        }
    }


    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = this.extractClaim(token, Claims::getExpiration);
            return expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Token đã hết hạn
        }
    }

    
    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Kiểm tra nếu là anonymous user
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            throw new RuntimeException("User chưa được xác thực (anonymous user)");
        }
        
        if (principal instanceof User) {
            return ((User) principal).getEmail();
        } else if (principal instanceof String) {
            return (String) principal;
        } else {
            return authentication.getName();
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Kiểm tra nếu là anonymous user
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            throw new RuntimeException("User chưa được xác thực (anonymous user)");
        }
        
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("Principal không phải là User object. Principal type: " + 
                principal.getClass().getName() + ", value: " + principal);
        }
    }

    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
