package com.example.controller;

import com.example.security.AuthJwt;
import com.example.security.AuthJwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthJwtProvider jwtProvider;

    @DisplayName("Hello 출력하기 - 성공")
    @Test
    void hello() throws Exception {
        AuthJwt jwt = jwtProvider.createJwt("test", "ROLE_USER");
        String token = "Bearer " + jwt.encode();

        mockMvc.perform(get("/hello")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"));
    }

    @DisplayName("Hello 출력하기 - 실패")
    @Test
    void hello_with_noAuthorization() throws Exception {
        mockMvc.perform(get("/hello"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
