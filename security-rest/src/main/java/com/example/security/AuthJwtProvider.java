package com.example.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class AuthJwtProvider {

    static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final AuthJwtProperties authJwtProperties;

    public AuthJwt createJwt(String subject, String auth) {
        return createJwt(subject, auth, LocalDateTime.now().plusSeconds(authJwtProperties.getValidityTerm()));
    }

    public AuthJwt decode(String encodedJwt) {
        return createJwt(getClaims(encodedJwt));
    }

    private AuthJwt createJwt(Claims claims) {
        String subject = claims.getSubject();
        String auth = claims.get("auth").toString();
        LocalDateTime expiredDtm = claims.getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return createJwt(subject, auth, expiredDtm);
    }

    private Claims getClaims(String encodedJwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(authJwtProperties.getKey())
                    .build()
                    .parseClaimsJws(encodedJwt)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomAuthenticationException("유효하지 않은 토큰입니다.");
        }
    }

    private AuthJwt createJwt(String subject, String auth, LocalDateTime expiredDtm) {
        return AuthJwt.builder()
                .algorithm(SIGNATURE_ALGORITHM)
                .subject(subject)
                .auth(auth)
                .expiredDtm(expiredDtm)
                .key(authJwtProperties.getKey())
                .build();
    }

    public AuthJwt decodeExpiredJwt(String encodedJwt) {
        return createJwt(getClaimsOfExpiredJwt(encodedJwt));
    }

    private Claims getClaimsOfExpiredJwt(String encodeJwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(authJwtProperties.getKey())
                    .build()
                    .parseClaimsJws(encodeJwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e2) {
            throw new CustomAuthenticationException("유효하지 않은 토큰입니다.");
        }
    }
}
