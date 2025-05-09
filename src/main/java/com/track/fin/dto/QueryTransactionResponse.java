package com.track.fin.dto;

import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QueryTransactionResponse {

    private String accountNumber;

    private TransactionType transactionType;

    private TransactionResultType transactionResult;

    private String transactionId;

    private Long amount;


    public static QueryTransactionResponse from(TransactionDto transactionDto) {
        return QueryTransactionResponse.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .transactionType(transactionDto.getTransactionType())
                .transactionResult(transactionDto.getTransactionResultType())
                .transactionId(transactionDto.getId())
                .amount(transactionDto.getAmount())
                .build();
    }

}
