package com.track.fin.record;

import com.track.fin.domain.Account;
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

    public static AccountRecord from(Account account) {
        return new AccountRecord(
                account.getUser().getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType(),
                account.getRegisterdAt(),
                account.getUnregisteredAt()
        );
    }

}
