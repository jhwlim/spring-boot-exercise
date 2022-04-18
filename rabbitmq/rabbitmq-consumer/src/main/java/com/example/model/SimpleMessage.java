package com.example.model;

public class SimpleMessage {

    private String message;

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SimpleMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
