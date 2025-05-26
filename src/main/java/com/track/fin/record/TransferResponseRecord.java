package com.track.fin.record;

import com.track.fin.domain.Transaction;
import com.track.fin.type.TransactionType;

import java.time.LocalDateTime;

public record TransferResponseRecord(

        String transactionId,
        String fromAccountNumber,
        String toAccountNumber,
        Long amount,
        Long fromBalanceSnapshot,
        Long toBalanceSnapshot,
        LocalDateTime transactionDate,
        TransactionType transactionType

) {

    public static TransferResponseRecord from(Transaction fromTransaction, Transaction toTransaction) {
        return new TransferResponseRecord(
                fromTransaction.getId(),
                fromTransaction.getAccount().getAccountNumber(),
                toTransaction.getAccount().getAccountNumber(),
                fromTransaction.getAmount(),
                fromTransaction.getBalanceSnapshot(),
                toTransaction.getBalanceSnapshot(),
                fromTransaction.getTransactionDate(),
                fromTransaction.getTransactionType()
        );
    }

    public static TransferResponseRecord from(Transaction transaction) {
        return new TransferResponseRecord(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                null,
                transaction.getAmount(),
                transaction.getBalanceSnapshot(),
                null,
                transaction.getTransactionDate(),
                transaction.getTransactionType()
        );
    }

}
