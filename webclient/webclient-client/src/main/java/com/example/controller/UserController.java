package com.example.controller;

import com.example.dto.UserDto;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<UserDto>> getUserById(@PathVariable long id) {
        Mono<UserDto> userMono = userService.getUserById(id);
        return userMono.map(ResponseEntity::ok);
    }

}
