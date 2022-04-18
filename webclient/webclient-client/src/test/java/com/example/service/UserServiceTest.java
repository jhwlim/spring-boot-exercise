package com.example.service;

import com.example.client.UserClient;
import com.example.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserClient userClient;

    @Test
    void getUserById_whenThrowClientRequestException() {
        doReturn(Mono.error(new Exception())).when(userClient).getUserById(anyLong());

        Mono<UserDto> userDto = userService.getUserById(anyLong());
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> userDto.block());


//        assertThat(user.block()).isNull();
//        assertThat(user).isEqualTo(Mono.empty());
    }
}
