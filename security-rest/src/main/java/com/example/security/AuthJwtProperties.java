package com.example.security;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.security.Key;

@Getter
@ConstructorBinding
@ConfigurationProperties("jwt")
public class AuthJwtProperties {

    private final String secretKey;
    private final long validityTerm;
    private final Key key;

    private final long refreshValidityTerm;

    public AuthJwtProperties(String secretKey, long validityTerm, long refreshValidityTerm) {
        this.secretKey = secretKey;
        this.validityTerm = validityTerm;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.refreshValidityTerm = refreshValidityTerm;
    }

}
