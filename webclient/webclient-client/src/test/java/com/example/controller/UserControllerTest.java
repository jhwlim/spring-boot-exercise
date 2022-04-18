package com.example.controller;

import com.example.client.UserClient;
import com.example.dto.UserDto;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserClient userClient;

    @Autowired
    WebTestClient webTestClient;


    Gson gson = new Gson();

    @DisplayName("유저 조회 - 성공")
    @Test
    void getUserById() throws Exception {
        UserDto user = UserDto.builder()
                .name("테스트")
                .build();
        UserDto savedUser = userClient.saveUser(user).block();
        assertThat(savedUser.getId()).isNotNull();
        long id = savedUser.getId();
        
        MvcResult mvcResult = mockMvc.perform(get("/users/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        userClient.removeUser(id).block();
    }

    @DisplayName("유저 조회 - 실패")
    @Test
    void getUserById_withUserNotFound() throws Exception {
        long id = 0L;
        MvcResult mvcResult = mockMvc.perform(get("/users/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("유저 조회 (by WebTestClient) - 성공")
    @Test
    void getUserById_withWebTestClient() {
        UserDto user = UserDto.builder()
                .name("테스트4")
                .build();
        UserDto savedUser = userClient.saveUser(user).block();
        assertThat(savedUser.getId()).isNotNull();
        long id = savedUser.getId();

        WebTestClient.ResponseSpec exchange = webTestClient.get()
                .uri("/users/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        UserDto actualUser = exchange.expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(actualUser.getId()).isEqualTo(id);
        assertThat(actualUser.getName()).isEqualTo(user.getName());

        userClient.removeUser(id).block();
    }
    
}
