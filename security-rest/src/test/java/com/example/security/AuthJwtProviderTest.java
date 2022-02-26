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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

    String subject = "test";
    String auth = "ROLE_USER";
    LocalDateTime expiredDtm = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void setup() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    @DisplayName("JWT 생성하기")
    @Test
    void createJwt() {
        AuthJwt jwt = jwtProvider.createJwt(subject, auth);

        assertThat(jwt.getSubject()).isEqualTo(subject);
        assertThat(jwt.getAuth()).isEqualTo(auth);
    }

    @DisplayName("JWT 디코딩하기")
    @Test
    void decode() {
        String encodedJwt = createJwt(subject, auth, expiredDtm).encode();

        AuthJwt decodedJwt = jwtProvider.decode(encodedJwt);

        assertThat(decodedJwt.getSubject()).isEqualTo(subject);
        assertThat(decodedJwt.getAuth()).isEqualTo(auth);
        assertThat(decodedJwt.getExpiredDtm()).isEqualToIgnoringNanos(expiredDtm);
    }

    @DisplayName("JWT 디코딩하기 - 실패 (키가 일치하지 않는 경우)")
    @Test
    void decode_with_keyNotMatches() {
        Key wrongKey = Keys.hmacShaKeyFor((secretKey + "0123456789").getBytes());
        String encodedJwt = createJwt(wrongKey, subject, auth, expiredDtm).encode();

        assertThatExceptionOfType(CustomAuthenticationException.class).isThrownBy(() -> {
            jwtProvider.decode(encodedJwt);
        });
    }

    @DisplayName("JWT 디코딩하기 - 실패 (유효시간이 만료된 경우)")
    @Test
    void decode_with_expiredOver() {
        LocalDateTime pastExpiredDtm = LocalDateTime.now().minusHours(1L);
        String encodedJwt = createJwt(subject, auth, pastExpiredDtm).encode();

        assertThatExceptionOfType(CustomAuthenticationException.class).isThrownBy(() -> {
            jwtProvider.decode(encodedJwt);
        });
    }

    private AuthJwt createJwt(String subject, String auth, LocalDateTime expiredDtm) {
        return createJwt(key, subject, auth, expiredDtm);
    }

    private AuthJwt createJwt(Key wrongKey, String subject, String auth, LocalDateTime expiredDtm) {
        return AuthJwt.builder()
                .algorithm(AuthJwtProvider.SIGNATURE_ALGORITHM)
                .subject(subject)
                .auth(auth)
                .expiredDtm(expiredDtm)
                .key(wrongKey)
                .build();
    }

}
