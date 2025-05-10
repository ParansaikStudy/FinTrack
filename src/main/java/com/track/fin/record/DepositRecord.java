package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record DepositRecord(

        String transactionId,
        String accountNumber,
        Long amount,
        Long balanceSnapshot

) {

    public static DepositRecord from(Transaction transaction) {
        return new DepositRecord(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getBalanceSnapshot()
        );
    }

}
