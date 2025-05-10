package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record WithdrawalRecord(

        String transactionId,
        String accountNumber,
        Long amount,
        Long balanceSnapshot

) {
    public static WithdrawalRecord from(Transaction transaction) {
        return new WithdrawalRecord(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getBalanceSnapshot()
        );
    }

}
