package com.example.service;

import com.example.model.SimpleMessage;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public void printOutSimpleMessage(SimpleMessage message) {
        System.out.println("Consume Simple Message : " + message);
    }

}
