package com.track.fin.dto;

import com.track.fin.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private Long userId;

    private String accountNumber;

    private Long balance;

    private LocalDateTime registeredAt;

    private LocalDateTime unregisteredAt;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .build();
    }
}
