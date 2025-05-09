package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record TransferResponse(
        String transactionId,
        String fromAccountNumber,
        String toAccountNumber,
        Long amount,
        Long fromBalanceSnapshot,
        Long toBalanceSnapshot
) {
    public static TransferResponse from(Transaction fromTransaction, Transaction toTransaction) {
        return new TransferResponse(
                fromTransaction.getId(),
                fromTransaction.getAccount().getAccountNumber(),
                toTransaction.getAccount().getAccountNumber(),
                fromTransaction.getAmount(),
                fromTransaction.getBalanceSnapshot(),
                toTransaction.getBalanceSnapshot()
        );
    }
}
