package com.example.model.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthenticationRequest {

    private String username;
    private String password;

    @Builder
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
