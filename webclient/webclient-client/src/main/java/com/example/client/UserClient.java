package com.example.client;

import com.example.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final WebClient webClient;

    public Mono<UserDto> saveUser(UserDto user) {
        return webClient.post()
                .uri("/users")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<Void> removeUser(long id) {
        return webClient.delete()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<UserDto> getUserById(long id) {
        return webClient.get()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

}
