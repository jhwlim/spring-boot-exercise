package com.example.service.auth;

import com.example.domain.account.Account;
import com.example.domain.account.AccountRepository;
import com.example.domain.auth.RefreshToken;
import com.example.domain.auth.RefreshTokenRepository;
import com.example.security.CustomAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.refresh-validity-term}")
    private Long refreshValidityTerm;

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String createRefreshToken(String nickname) {
        Account account = accountRepository.findByNickname(nickname).orElseThrow(() -> new UsernameNotFoundException(nickname + "의 닉네임을 가진 계정이 존재하지 않습니다."));
        RefreshToken refreshToken = refreshTokenRepository.findByAccountNickname(nickname)
                .orElseGet(() -> RefreshToken.builder()
                        .value(UUID.randomUUID().toString())
                        .expiredDtm(LocalDateTime.now().plusSeconds(refreshValidityTerm))
                        .account(account)
                        .build());
        RefreshToken createdRefreshToken = refreshTokenRepository.save(refreshToken);
        return createdRefreshToken.getValue();
    }

    public void validateRefreshToken(String nickname, String refreshToken) {
        RefreshToken savedRefreshToken = refreshTokenRepository.findByAccountNickname(nickname)
                .orElseThrow(() -> new CustomAuthenticationException("저장된 토큰이 존재하지 않습니다."));
        if (!savedRefreshToken.equalsValue(refreshToken)) {
            throw new CustomAuthenticationException("토큰이 일치하지 않습니다.");
        }

        if (savedRefreshToken.isExpired()) {
            throw new CustomAuthenticationException("토큰이 만료되었습니다.");
        }
    }

}
