package com.example.service;

import com.example.producer.ExampleProducer;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    private final ExampleProducer exampleProducer;

    public ExampleService(ExampleProducer exampleProducer) {
        this.exampleProducer = exampleProducer;
    }

    public void sendHello() {
        exampleProducer.sendHello();
    }

}
