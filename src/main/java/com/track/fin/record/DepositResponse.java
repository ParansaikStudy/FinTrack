package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record DepositResponse(
        String transactionId,
        String accountNumber,
        Long amount,
        Long balanceSnapshot
) {

    public static DepositResponse from(Transaction transaction) {
        return new DepositResponse(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getBalanceSnapshot()
        );
    }
}
