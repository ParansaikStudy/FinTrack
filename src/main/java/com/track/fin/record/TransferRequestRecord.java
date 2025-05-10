package com.track.fin.record;

public record TransferRequestRecord(
        Long userId,
        String fromAccountNumber,
        String toAccountNumber,
        Long amount
) {
}

