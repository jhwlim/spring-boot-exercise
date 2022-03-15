package com.example.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthJwtProvider jwtProvider;

    @DisplayName("권한 확인하기")
    @Test
    void authorize() throws Exception {
        AuthJwt jwt = jwtProvider.createJwt("test", "ROLE_USER");
        String token = CustomAuthorizationFilter.PREFIX_OF_AUTHORIZATION_HEADER + jwt.encode();
        mockMvc.perform(get("/test")
                        .header(CustomAuthorizationFilter.AUTHORIZATION_HEADER, token))
                .andExpect(status().isOk());
    }

    @DisplayName("권한 확인하기 - 실패 (401, 유효하지 않은 토큰인 경우)")
    @Test
    void authorize_with_invalidToken() throws Exception {
        AuthJwt jwt = jwtProvider.createJwt("test", "ROLE_USER");
        String token = "Bearerr " + jwt.encode();
        mockMvc.perform(get("/test")
                        .header(CustomAuthorizationFilter.AUTHORIZATION_HEADER, token))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("권한 확인하기 - 실패 (403, 권한이 없는 경우)")
    @Test
    void authorize_with_noAuthorization() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isForbidden());
    }

}
