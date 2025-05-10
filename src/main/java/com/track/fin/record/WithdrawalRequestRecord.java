package com.track.fin.record;

public record WithdrawalRequestRecord(

        Long userId,
        String accountNumber,
        Long amount

) {
}

