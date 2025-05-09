package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record DepositRequest(
        Long userId,
        String accountNumber,
        Long amount
) {
}

