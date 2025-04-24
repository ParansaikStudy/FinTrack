package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record TransferRequest(
        Long userId,
        String fromAccountNumber,
        String toAccountNumber,
        Long amount
) {
}

