package com.track.fin.record;

import com.track.fin.type.AccountType;

public record CreateAccount(

        Long userId,
        Long initialBalance,
        AccountType accountType

) {
}
