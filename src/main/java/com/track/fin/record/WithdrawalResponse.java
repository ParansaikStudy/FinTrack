package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record WithdrawalResponse(
        String transactionId,
        String accountNumber,
        Long amount,
        Long balanceSnapshot
) {
    public static WithdrawalResponse from(Transaction transaction) {
        return new WithdrawalResponse(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getBalanceSnapshot()
        );
    }

}
