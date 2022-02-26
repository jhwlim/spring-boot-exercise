package com.example.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class CustomAuthorizationFilter extends BasicAuthenticationFilter {
    static final String AUTHORIZATION_HEADER = "Authorization";
    static final String PREFIX_OF_AUTHORIZATION_HEADER = "Bearer ";

    private final AuthJwtProvider jwtProvider;

    public CustomAuthorizationFilter(AuthenticationManager authenticationManager, AuthJwtProvider jwtProvider) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = getAuthorization(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            validateTokenType(token);
            AuthJwt jwt = jwtProvider.decode(getEncodedJwt(token));
            UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (AuthenticationException e) {
            onUnsuccessfulAuthentication(request, response, e);
        }
    }

    private String getAuthorization(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    private void validateTokenType(String token) {
        if (!token.startsWith(PREFIX_OF_AUTHORIZATION_HEADER)) {
            throw new CustomAuthenticationException("잘못된 형식의 토큰입니다.");
        }
    }

    private String getEncodedJwt(String token) {
        return token.substring(PREFIX_OF_AUTHORIZATION_HEADER.length());
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(AuthJwt jwt) {
        return new UsernamePasswordAuthenticationToken(
                jwt.getSubject(),
                "",
                Arrays.stream(jwt.getAuth().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                    );
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
