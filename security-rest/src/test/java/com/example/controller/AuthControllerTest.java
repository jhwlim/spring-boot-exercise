package com.example.controller;

import com.example.domain.auth.RefreshTokenRepository;
import com.example.model.auth.AuthTokenIssueRequest;
import com.example.security.AuthJwtProvider;
import com.example.service.auth.AuthService;
import com.example.utils.JsonParserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthJwtProvider jwtProvider;

    @Autowired
    AuthService authService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    String nickname = "test";
    String auth = "ROLE_USER";
    String accessToken;
    String refreshToken;

    @BeforeEach
    void setUp() {
        accessToken = jwtProvider.createJwt(nickname, auth).encode();
        refreshToken = authService.createRefreshToken(nickname);
    }

    @DisplayName("Access Token 갱신하기")
    @Test
    void getToken() throws Exception {
        AuthTokenIssueRequest request = createRequest(accessToken, refreshToken);

        mockMvc.perform(post("/token")
                        .content(JsonParserUtils.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("token_type").value("Bearer"))
                .andExpect(jsonPath("access_token").hasJsonPath())
                .andExpect(jsonPath("refresh_token").value(request.getRefreshToken()));
    }

    private AuthTokenIssueRequest createRequest(String accessToken, String refreshToken) {
        return AuthTokenIssueRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @DisplayName("Access Token 갱신하기 - 실패 (request parameters 유효성 검사 실패)")
    @ParameterizedTest
    @CsvSource(value = {"not empty,", ",not empty"})
    void getToken_with_invalidRequestParameters(String accessToken, String refreshToken) throws Exception {
        AuthTokenIssueRequest request = createRequest(accessToken, refreshToken);

        mockMvc.perform(post("/token")
                        .content(JsonParserUtils.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Access Token 갱신하기 - 실패 (refresh token과 일치하지 않는 경우)")
    @Test
    void getToken_with_notMatchesRefreshToken() throws Exception {
        AuthTokenIssueRequest request = createRequest(accessToken, refreshToken + "0000");

        mockMvc.perform(post("/token")
                        .content(JsonParserUtils.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
