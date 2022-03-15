package com.example.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AuthJwtTest {

    String secretKey = "aGVsbG8gc3ByaW5nIGJvb3Qgd2l0aCBzZWN1cml0eSBmb3IgcmVzdCBhcGkK";
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
    Long validityTerm = 3600L;
    SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

    @DisplayName("JWT 인코딩하기")
    @Test
    void encode() {
        String subject = "test";
        String auth = "ROLE_USER";
        LocalDateTime expiredDtm = LocalDateTime.of(2022, 1, 1, 0, 0, 0).plusSeconds(validityTerm);
        AuthJwt jwt = AuthJwt.builder()
                .algorithm(algorithm)
                .subject(subject)
                .auth(auth)
                .expiredDtm(expiredDtm)
                .key(key)
                .build();

        String encoded = jwt.encode();

        assertThat(encoded).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTY0MDk2NjQwMH0.VDKVZg5h5oG4GfVdtxRLj2MkpBp8i-7JJsAoQwSEaxg");
    }

}
