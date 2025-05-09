package com.track.fin.dto;

import com.track.fin.domain.Transaction;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private String id;

    private String accountNumber;

    private TransactionType transactionType;

    private TransactionResultType transactionResultType;

    private Long amount;

    private Long balanceSnapshot;


    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .build();
    }

}
