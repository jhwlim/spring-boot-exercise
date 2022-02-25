package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class AuthJwtProvider {

    static final String PREFIX_OF_AUTHORIZATION_HEADER = "Bearer ";
    static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final Long validityTerm;
    private final Key key;

    public AuthJwtProvider(@Value("${jwt.secret-key}") String secretKey,
                           @Value("${jwt.validity-term}") Long validityTerm) {
        this.validityTerm = validityTerm;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public AuthJwt createJwt(String subject, String auth) {
        return createJwt(subject, auth, LocalDateTime.now().plusSeconds(validityTerm));
    }

    public AuthJwt decode(String token) {
        validateTokenType(token);

        Claims claims = getClaims(getEncodedJwt(token));
        String subject = claims.getSubject();
        String auth = claims.get("auth").toString();
        LocalDateTime expiredDtm = claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return createJwt(subject, auth, expiredDtm);
    }

    private void validateTokenType(String token) {
        if (!token.startsWith(PREFIX_OF_AUTHORIZATION_HEADER)) {
            throw new CustomAuthenticationException("잘못된 형식의 토큰입니다.");
        }
    }

    private String getEncodedJwt(String token) {
        return token.substring(PREFIX_OF_AUTHORIZATION_HEADER.length());
    }

    private Claims getClaims(String encodedJwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(encodedJwt)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new CustomAuthenticationException("유효하지 않은 토큰입니다.");
        }
    }

    private AuthJwt createJwt(String subject, String auth, LocalDateTime expiredDtm) {
        return AuthJwt.builder()
                .algorithm(SIGNATURE_ALGORITHM)
                .subject(subject)
                .auth(auth)
                .expiredDtm(expiredDtm)
                .key(key)
                .build();
    }

}
