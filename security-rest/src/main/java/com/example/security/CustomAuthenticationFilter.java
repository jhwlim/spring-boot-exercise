package com.example.security;

import com.example.model.auth.AuthenticationRequest;
import com.example.model.auth.AuthenticationResponse;
import com.example.service.auth.AuthService;
import com.example.utils.JsonParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthJwtProvider jwtProvider;
    private final AuthService authService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, AuthJwtProvider jwtProvider, AuthService authService) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.authService = authService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthenticationRequest authenticationRequest = JsonParserUtils.toObject(getContent(request), AuthenticationRequest.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        return getAuthenticationManager().authenticate(token);
    }

    private String getContent(HttpServletRequest request) {
        try {
            return IOUtils.toString(request.getReader());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomAuthenticationException("잘못된 요청입니다.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        response.setContentType("application/json");

        AuthJwt jwt = jwtProvider.createJwt(authResult.getName(), authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        String accessToken = jwt.encode();
        String refreshToken = authService.createRefreshToken(authResult.getName());

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(refreshToken)
                .build();
        response.getWriter().write(JsonParserUtils.toJson(authenticationResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

}
