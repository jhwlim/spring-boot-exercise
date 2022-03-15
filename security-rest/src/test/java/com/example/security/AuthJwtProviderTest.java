package com.example.security;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Key;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthJwtProviderTest {

    @InjectMocks
    AuthJwtProvider jwtProvider;

    @Mock
    AuthJwtProperties jwtProperties;

    String secretKey = "xlbyBwcmV0aXVtIHBS4gVmVzdGlidWx1bSBuZXF1ZSB1cm5hLCB2b2x1dHBhdCBzZWQgYXVndWUgdXQsIGx1Y3R1cyBibGFuZGl0IGV4LiBJbnRlZ2VyIGdyYXZpZGEgbW9sbGlzIG1pIGlkIGZpbmlidXMuCg";
    Long validityTerm = 3600L;
    Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    String subject = "test";
    String auth = "ROLE_USER";
    LocalDateTime expiredDtm = LocalDateTime.now().plusHours(1);

    @DisplayName("JWT 생성하기")
    @Test
    void createJwt() {
        when(jwtProperties.getValidityTerm()).thenReturn(validityTerm);

        AuthJwt jwt = jwtProvider.createJwt(subject, auth);

        assertThat(jwt.getSubject()).isEqualTo(subject);
        assertThat(jwt.getAuth()).isEqualTo(auth);
        assertThat(jwt.getExpiredDtm()).isEqualToIgnoringMinutes(LocalDateTime.now().plusSeconds(validityTerm));
    }

    @DisplayName("JWT 디코딩하기")
    @Test
    void decode() {
        when(jwtProperties.getKey()).thenReturn(key);

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

        assertThatExceptionOfType(CustomAuthenticationException.class).isThrownBy(() -> jwtProvider.decode(encodedJwt));
    }

    @DisplayName("JWT 디코딩하기 - 실패 (유효시간이 만료된 경우)")
    @Test
    void decode_with_expiredOver() {
        when(jwtProperties.getKey()).thenReturn(key);

        LocalDateTime pastExpiredDtm = LocalDateTime.now().minusHours(1L);
        String encodedJwt = createJwt(subject, auth, pastExpiredDtm).encode();

        assertThatExceptionOfType(CustomAuthenticationException.class).isThrownBy(() -> jwtProvider.decode(encodedJwt));
    }

    private AuthJwt createJwt(String subject, String auth, LocalDateTime expiredDtm) {
        return createJwt(jwtProperties.getKey(), subject, auth, expiredDtm);
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
