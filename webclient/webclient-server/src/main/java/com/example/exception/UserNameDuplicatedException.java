package com.example.exception;

public class UserNameDuplicatedException extends RuntimeException {

    public UserNameDuplicatedException() {
        super("이미 사용 중인 이름입니다.");
    }

}
