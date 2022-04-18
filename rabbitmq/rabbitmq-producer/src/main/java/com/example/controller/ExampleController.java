package com.example.controller;

import com.example.service.ExampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @PostMapping("/message/hello")
    public ResponseEntity<Void> sendHello() {
        exampleService.sendHello();
        return ResponseEntity.ok().build();
    }

}
