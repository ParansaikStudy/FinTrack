package com.track.fin.record;

public record DepositTransactionRecord(

        Long userId,
        String accountNumber,
        Long amount

) {
}

