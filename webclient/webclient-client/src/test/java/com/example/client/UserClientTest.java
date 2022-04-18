package com.example.client;

import com.example.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserClientTest {

    @Autowired
    UserClient client;

    @DisplayName("유저 삭제 - 실패")
    @Test
    void removeUser() {
        assertThatExceptionOfType(WebClientResponseException.class)
                .isThrownBy(() -> client.removeUser(1L).block());
    }

    @DisplayName("유저 등록 - 성공")
    @Test
    void saveUser() {
        UserDto user = UserDto.builder()
                .name("홍길동")
                .build();

        Mono<UserDto> resultOfClient = client.saveUser(user);
        UserDto actual = resultOfClient.block();

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("홍길동");

        client.removeUser(actual.getId()).block();
    }

    @DisplayName("유저 등록 - 실패 (이름이 중복 되는 경우)")
    @Test
    void saveUser_fail() {

    }

    @DisplayName("id를 이용하여 유저 조회 - 성공")
    @Test
    void getUserById() {
        UserDto user = UserDto.builder()
                .name("홍길동")
                .build();

        Mono<UserDto> resultOfSaveRequest = client.saveUser(user);
        UserDto savedUser = resultOfSaveRequest.block();

        Mono<UserDto> resultOfClient = client.getUserById(savedUser.getId());
        UserDto actual = resultOfClient.block();

        assertThat(actual.getId()).isEqualTo(savedUser.getId());
        assertThat(actual.getName()).isEqualTo(savedUser.getName());

        client.removeUser(savedUser.getId()).block();
    }

    @DisplayName("id를 이용하여 유저 조회 - 실패")
    @Test
    void getUserById_whenUserNotFound() {
        Long id = 0L;
        assertThatExceptionOfType(WebClientResponseException.class)
                .isThrownBy(() -> client.getUserById(id).block());
    }

}
