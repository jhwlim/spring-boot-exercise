package com.example.security;

import com.example.domain.auth.RefreshToken;
import com.example.domain.auth.RefreshTokenRepository;
import com.example.model.auth.AuthenticationRequest;
import com.example.model.auth.AuthenticationResponse;
import com.example.utils.JsonParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthJwtProvider jwtProvider;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.validity-term}")
    Long validityTerm;

    @Value("${jwt.refresh-validity-term}")
    Long refreshValidityTerm;

    @Transactional
    @DisplayName("로그인 - 성공")
    @Test
    void authenticate() throws Exception {
        String username = "test";
        String content = JsonParserUtils.toJson(AuthenticationRequest.builder()
                .username(username)
                .password("1234")
                .build());

        MvcResult result = mockMvc.perform(post("/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("access_token").hasJsonPath())
                .andExpect(jsonPath("token_type").value("Bearer"))
                .andExpect(jsonPath("refresh_token").hasJsonPath())
                .andReturn();
        AuthenticationResponse response = getResponseBody(result);
        AuthJwt jwt = jwtProvider.decode(response.getAccessToken());
        LocalDateTime now = LocalDateTime.now();

        assertThat(jwt.getSubject()).isEqualTo(username);
        assertThat(jwt.getAuth()).isEqualTo("ROLE_USER");
        assertThat(jwt.getExpiredDtm()).isBetween(now, now.plusSeconds(validityTerm));

        RefreshToken refreshToken = refreshTokenRepository.findByAccountNickname(username).orElse(null);
        assertThat(refreshToken.getValue()).isEqualTo(response.getRefreshToken());
        assertThat(refreshToken.getExpiredDtm()).isBetween(now.plusSeconds(refreshValidityTerm - 3600), now.plusSeconds(refreshValidityTerm));
    }

    private AuthenticationResponse getResponseBody(MvcResult result) throws UnsupportedEncodingException {
        MockHttpServletResponse response = result.getResponse();
        return JsonParserUtils.toObject(response.getContentAsString(), AuthenticationResponse.class);
    }

    @DisplayName("로그인 - 실패 (닉네임이 존재하지 않는 경우)")
    @Test
    void authenticate_with_nicknameNotExists() throws Exception {
        String content = JsonParserUtils.toJson(AuthenticationRequest.builder()
                .username("tttt")
                .password("1234")
                .build());
        mockMvc.perform(post("/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("로그인 - 실패 (비밀번호가 일치하지 않는 경우)")
    @Test
    void authenticate_with_passwordNotMatches() throws Exception {
        String content = JsonParserUtils.toJson(AuthenticationRequest.builder()
                .username("test")
                .password("1111")
                .build());
        mockMvc.perform(post("/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
