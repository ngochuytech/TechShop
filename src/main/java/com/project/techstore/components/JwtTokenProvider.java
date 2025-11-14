package com.project.techstore.components;

import com.project.techstore.exceptions.*;
import com.project.techstore.models.Token;
import com.project.techstore.models.User;
import com.project.techstore.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate );

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

    public String getUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token đã hết hạn. Vui lòng đăng nhập lại");
        } catch (MalformedJwtException e) {
            throw new MalformedTokenException("Token không đúng định dạng", e);
        } catch (SignatureException e) {
            throw new TokenSignatureException("Chữ ký token không hợp lệ", e);
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedTokenException("Token không được hỗ trợ", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Token trống hoặc không hợp lệ", e);
        } catch (JwtException e) {
            throw new InvalidTokenException("Token không hợp lệ: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token, User user) {
        try {
            // Email
            String subject = extractClaim(token, Claims::getSubject);

            // Kiểm tra token tồn tại trong DB ko ?
            Token existingToken = tokenRepository.findByToken(token);
            if(existingToken == null) {
                throw new InvalidTokenException("Token không tồn tại trong hệ thống");
            }
            if(existingToken.isRevoked()) {
                throw new RevokedTokenException("Token đã bị thu hồi. Vui lòng đăng nhập lại");
            }
            if(!user.getIsActive()) {
                throw new UnauthorizedException("Tài khoản đã bị vô hiệu hóa");
            }
            if(isTokenExpired(token)) {
                throw new ExpiredTokenException("Token đã hết hạn. Vui lòng đăng nhập lại");
            }
            return subject.equals(user.getUsername());
        } catch (ExpiredTokenException | RevokedTokenException | UnauthorizedException | InvalidTokenException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token đã hết hạn. Vui lòng đăng nhập lại");
        } catch (MalformedJwtException e) {
            throw new MalformedTokenException("Token không đúng định dạng", e);
        } catch (SignatureException e) {
            throw new TokenSignatureException("Chữ ký token không hợp lệ", e);
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedTokenException("Token không được hỗ trợ", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Token trống hoặc không hợp lệ", e);
        } catch (JwtException e) {
            throw new InvalidTokenException("Token không hợp lệ: " + e.getMessage(), e);
        }
    }
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build() 
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token đã hết hạn. Vui lòng đăng nhập lại");
        } catch (MalformedJwtException e) {
            throw new MalformedTokenException("Token không đúng định dạng", e);
        } catch (SignatureException e) {
            throw new TokenSignatureException("Chữ ký token không hợp lệ", e);
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedTokenException("Token không được hỗ trợ", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Token trống hoặc không hợp lệ", e);
        } catch (JwtException e) {
            throw new InvalidTokenException("Token không hợp lệ: " + e.getMessage(), e);
        }
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
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
            throw new UnauthorizedException("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Kiểm tra nếu là anonymous user
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            throw new UnauthorizedException("Bạn chưa đăng nhập. Vui lòng đăng nhập để tiếp tục");
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
            throw new UnauthorizedException("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập");
        }
        
        Object principal = authentication.getPrincipal();
        
        // Kiểm tra nếu là anonymous user
        if (principal instanceof String && "anonymousUser".equals(principal)) {
            throw new UnauthorizedException("Bạn chưa đăng nhập. Vui lòng đăng nhập để tiếp tục");
        }
        
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new JwtAuthenticationException("Principal không phải là User object. Principal type: " + 
                principal.getClass().getName() + ", value: " + principal);
        }
    }

    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
