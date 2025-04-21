package com.track.fin.dto;

import com.track.fin.domain.Account;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 삭제 예정
public class AccountDto {

    private Long userId;

    private String accountNumber;

    private Long balance;

    private AccountType accountType;

    private AccountStatus accountStatus;

    private LocalDateTime registeredAt;

    private LocalDateTime unregisteredAt;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .build();
    }
}
