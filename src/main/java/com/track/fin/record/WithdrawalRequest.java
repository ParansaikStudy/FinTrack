package com.track.fin.record;

public record WithdrawalRequest(
        Long userId,
        String accountNumber,
        Long amount
) {
}

