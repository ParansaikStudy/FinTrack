package com.track.fin.record;

import com.track.fin.type.TransactionMethodType;
import jakarta.validation.constraints.*;

public record TransferRequestRecord(

        @NotNull
        @Min(1)
        Long userId,

        @NotBlank
        @Size(min = 10, max = 10)
        String fromAccountNumber,

        @NotBlank
        @Size(min = 10, max = 10)
        String toAccountNumber,

        @NotNull
        @Min(10)
        @Max(1_000_000_000)
        Long amount,

        @NotNull
        TransactionMethodType transactionMethodType
) {
}

