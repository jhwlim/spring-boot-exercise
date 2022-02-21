package com.example.domain.account;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @DisplayName("초기 데이터 정보 확인하기")
    @Test
    public void initializeDatabase() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Account account = accountRepository.findById(1l).orElse(null);
        assertThat(account).isNotNull();
        assertThat(account.getNickname()).isEqualTo("test");
        assertThat(encoder.matches("1234", account.getPassword())).isTrue();
    }

}
