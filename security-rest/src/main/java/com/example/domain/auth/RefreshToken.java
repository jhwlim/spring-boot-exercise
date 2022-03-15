package com.example.domain.auth;

import com.example.domain.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class RefreshToken {

    @Id
    @GeneratedValue
    private Long id;

    private String value;

    private LocalDateTime expiredDtm;

    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    @Builder
    public RefreshToken(Long id, String value, LocalDateTime expiredDtm, Account account) {
        this.id = id;
        this.value = value;
        this.expiredDtm = expiredDtm;
        this.account = account;
    }

    public boolean equalsValue(String value) {
        return this.value.equals(value);
    }

    public boolean isExpired() {
        return expiredDtm.isBefore(LocalDateTime.now());
    }

}
