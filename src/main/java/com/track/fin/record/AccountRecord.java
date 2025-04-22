package com.track.fin.record;

import com.track.fin.domain.Account;
import com.track.fin.dto.AccountDto;
import com.track.fin.type.AccountType;

import java.time.LocalDateTime;

public record AccountRecord(

        Long userId,
        String accountNumber,
        Long balance,
        AccountType accountType,
        LocalDateTime registeredAt,
        LocalDateTime unregisteredAt

) {

    public static AccountDto from(Account account) {
        return AccountDto.builder()
                .userId(account.getUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .build();
    }

}
