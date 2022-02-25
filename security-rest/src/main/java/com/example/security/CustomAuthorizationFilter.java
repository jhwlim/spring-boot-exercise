package com.example.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    private final AuthJwtProvider jwtProvider;

    public CustomAuthorizationFilter(AuthenticationManager authenticationManager, AuthJwtProvider jwtProvider) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = getAuthorization(request);
        if (token != null) {
            AuthJwt jwt = jwtProvider.decode(token);
            UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    private String getAuthorization(HttpServletRequest request) {
        return request.getHeader("Authorization");
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
}
