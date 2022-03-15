package com.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthJwt {

    private SignatureAlgorithm algorithm;
    private String subject;
    private String auth;
    private LocalDateTime expiredDtm;
    private Key key;

    @Builder
    public AuthJwt(SignatureAlgorithm algorithm, String subject, String auth, LocalDateTime expiredDtm, Key key) {
        this.algorithm = algorithm;
        this.subject = subject;
        this.auth = auth;
        this.expiredDtm = expiredDtm;
        this.key = key;
    }

    public String encode() {
        return Jwts.builder()
                .setSubject(subject)
                .claim("auth", auth)
                .setExpiration(getExpiredDate())
                .signWith(key, algorithm)
                .compact();
    }

    private Date getExpiredDate() {
        return Date.from(expiredDtm
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public String getSubject() {
        return subject;
    }

    public String getAuth() {
        return auth;
    }

    public LocalDateTime getExpiredDtm() {
        return expiredDtm;
    }
}
