package com.example.model.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthenticationResponse {

    private String accessToken;
    private String tokenType;
    private String refreshToken;

    @Builder
    public AuthenticationResponse(String accessToken, String tokenType, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
    }

}
