package ru.ivamly.chill.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.entity.enums.Role;

@Component
public class JwtProvider {

    @Value("${jwt.key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTime;
    
    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        List<String> authorities = user.getRoles().stream().map(GrantedAuthority::getAuthority).toList();
        Date exp = Date.from(Instant.now().plusMillis(expirationTime));

        return Jwts.builder()
                .signWith(key)
                .subject(user.getName())
                .claim(ChillClaim.USER_ID.getName(), user.getId())
                .claim(ChillClaim.ROLES.getName(), authorities)
                .expiration(exp)
                .compact();
    }

    public String getSubject(String token) {
        String username = parseClaims(token)
                .getPayload()
                .getSubject();

        if (username == null || username.isBlank()) {
            throw new JwtException("Отсутствует \"subject\" в токене");
        }

        return username;
    }

    public <T> T findClaim(String token, ChillClaim<T> chillClaim) {
        Claims body = parseClaims(token).getPayload();

        return chillClaim.extract(body);
    }

    private Jws<Claims> parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser().verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public static interface ChillClaim<T> {

        String getName();
    
        T extract(Claims claims);

        ChillClaim<UUID> USER_ID = new ChillClaim<>() {
            @Override
            public String getName() { 
                return "USER_ID";
            }

            @Override
            public UUID extract(Claims claims) {
                String userId = claims.get(getName(), String.class);

                return userId != null ? UUID.fromString(userId) : null;
            }
        };

        ChillClaim<Set<Role>> ROLES = new ChillClaim<>() {
            @Override
            public String getName() {
                return "ROLES";
            }

            @Override
            public Set<Role> extract(Claims claims) {
                List<String> roles = claims.get(getName(), List.class);

                if (roles == null) {
                    return Collections.emptySet();
                }
            
                return roles.stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet());
            }
        };
    }
}
