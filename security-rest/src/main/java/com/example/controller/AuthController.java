package com.example.controller;

import com.example.model.auth.AuthTokenIssueRequest;
import com.example.model.auth.AuthenticationResponse;
import com.example.security.AuthJwt;
import com.example.security.AuthJwtProvider;
import com.example.security.CustomAuthenticationException;
import com.example.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthJwtProvider jwtProvider;
    private final AuthService authService;

    /**
     * access token 갱신하기
     */
    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody @Valid AuthTokenIssueRequest request) {
        log.info("access token refresh request : {}", request);
        AuthJwt jwt = jwtProvider.decodeExpiredJwt(request.getAccessToken());
        authService.validateRefreshToken(jwt.getSubject(), request.getRefreshToken());

        AuthJwt newJwt = jwtProvider.createJwt(jwt.getSubject(), jwt.getAuth());
        return ResponseEntity.ok()
                .body(AuthenticationResponse.builder()
                        .tokenType("Bearer")
                        .accessToken(newJwt.encode())
                        .refreshToken(request.getRefreshToken())
                        .build());
    }

    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity handleAuthenticationException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }

}
