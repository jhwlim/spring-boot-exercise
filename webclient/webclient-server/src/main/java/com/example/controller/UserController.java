package com.example.controller;

import com.example.dto.UserDto;
import com.example.dto.UserRegisterRequest;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        return ResponseEntity
                .ok(userService.getUser(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegisterRequest request) {
        log.info("POST /users : {}", request);
        return ResponseEntity
                .ok(userService.saveUser(UserDto.builder()
                        .name(request.getName())
                        .build()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable long id) {
        log.info("DELETE /users/{}", id);
        userService.removeUser(id);
        return ResponseEntity
                .ok()
                .build();
    }

}
