package com.track.fin.record;

import com.track.fin.domain.Transaction;

public record TransferResponseRecord(

        String transactionId,
        String fromAccountNumber,
        String toAccountNumber,
        Long amount,
        Long fromBalanceSnapshot,
        Long toBalanceSnapshot

) {

    public static TransferResponseRecord from(Transaction fromTransaction, Transaction toTransaction) {
        return new TransferResponseRecord(
                fromTransaction.getId(),
                fromTransaction.getAccount().getAccountNumber(),
                toTransaction.getAccount().getAccountNumber(),
                fromTransaction.getAmount(),
                fromTransaction.getBalanceSnapshot(),
                toTransaction.getBalanceSnapshot()
        );
    }

}
