package uz.testplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.testplatform.entity.User;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT Util - token yaratish, parse qilish, tekshirish.
 *
 * Token ichida saqlanadi:
 *   - subject (email) - kim
 *   - role          - qaysi rol (USER/ADMIN)
 *   - issuedAt      - qachon yaratildi
 *   - expiration    - qachon tugaydi
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;


    // Kalit olish (Base64 dan)
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    // Token yaratish (email va role bilan)
    public String generateToken(User user) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }


    public LocalDateTime getExpirationTime() {
        Date expiration = new Date(System.currentTimeMillis() + expirationMs);
        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }


    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }


    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Token noto'g'ri: {}", e.getMessage());
            return false;
        }
    }


    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}