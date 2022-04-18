package com.example.service;

import com.example.client.UserClient;
import com.example.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;

    public Mono<UserDto> getUserById(long id) {
        log.info("UserService - getUserById, id : {}", id);
        return userClient.getUserById(id)
                .doOnError(e -> log.error("유저 정보를 조회하는데 실패하였습니다.", e));
    }

}
