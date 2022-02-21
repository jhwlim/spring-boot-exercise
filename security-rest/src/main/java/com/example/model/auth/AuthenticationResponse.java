package com.example.model.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthenticationResponse {

    private String accessToken;

    @Builder
    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}
