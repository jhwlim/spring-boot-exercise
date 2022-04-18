package com.example.controller;

import com.example.dto.UserDto;
import com.example.dto.UserRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson = new Gson();

    @Test
    void registerUser() throws Exception {
        UserRegisterRequest request = createUserRegisterRequest("홍길동");
        ResultActions perform = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(request)));

        MvcResult mvcResult = perform.andExpect(status().isOk())
                .andReturn();

        UserDto userDto = gson.fromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), UserDto.class);
        assertThat(userDto.getId()).isNotNull();
        assertThat(userDto.getName()).isEqualTo("홍길동");
    }

    private UserRegisterRequest createUserRegisterRequest(String name) {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName(name);
        return request;
    }

    @Test
    void getUser() {
    }

}
