package com.example.controller;

import com.example.model.auth.AuthTokenIssueRequest;
import com.example.model.auth.AuthenticationResponse;
import com.example.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * access token 갱신하기
     */
    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody @Valid AuthTokenIssueRequest request) {
        String newAccessToken = authService.regenerateAccessToken(request.getAccessToken(), request.getRefreshToken());
        return ResponseEntity.ok()
                .body(AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(request.getRefreshToken())
                        .build());
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity logout(@AuthenticationPrincipal String username) {
        authService.removeRefreshToken(username);
        return ResponseEntity.ok()
                .build();
    }

}
