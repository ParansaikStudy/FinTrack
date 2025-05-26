package com.track.fin.record;

import com.track.fin.type.TransactionMethodType;
import jakarta.validation.constraints.*;

public record WithdrawalRequestRecord(

        @NotNull
        @Min(1)
        Long userId,

        @NotBlank
        String accountNumber,

        @NotNull
        @Min(10)
        @Max(1000000000)
        Long amount,

        @NotNull
        TransactionMethodType transactionMethodType

) {
}
