package com.track.fin.dto;

import com.track.fin.type.TransactionResultType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UseBalance {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String accountNumber;

        private TransactionResultType transactionResult;

        private String transactionId;

        private Long amount;

        public static Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getId())
                    .amount(transactionDto.getAmount())
                    .build();
        }
    }

}
