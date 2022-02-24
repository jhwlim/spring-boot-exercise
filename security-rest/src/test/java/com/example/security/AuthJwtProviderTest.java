package com.example.security;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class AuthJwtProviderTest {

    @Autowired
    AuthJwtProvider jwtProvider;
    @Value("${jwt.secret-key}")
    String secretKey;
    @Value("${jwt.validity-term}")
    Long validityTerm;
    Key key;

    @BeforeEach
    void setup() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    @DisplayName("JWT 생성하기")
    @Test
    void createJwt() {
        String subject = "test";
        String auth = "ROLE_USER";

        AuthJwt jwt = jwtProvider.createJwt(subject, auth);

        assertThat(jwt.getSubject()).isEqualTo(subject);
        assertThat(jwt.getAuth()).isEqualTo(auth);
    }

    @DisplayName("JWT 디코딩하기")
    @Test
    void decode() {
        String subject = "test";
        String auth = "ROLE_USER";
        LocalDateTime expiredDtm = LocalDateTime.now().plusHours(1);
        AuthJwt jwt = AuthJwt.builder()
                .algorithm(AuthJwtProvider.SIGNATURE_ALGORITHM)
                .subject(subject)
                .auth(auth)
                .expiredDtm(expiredDtm)
                .key(key)
                .build();

        AuthJwt decodedJwt = jwtProvider.decode(jwt.encode());

        assertThat(decodedJwt.getSubject()).isEqualTo(subject);
        assertThat(decodedJwt.getAuth()).isEqualTo(auth);
        assertThat(decodedJwt.getExpiredDtm()).isEqualToIgnoringNanos(expiredDtm);
    }
}
