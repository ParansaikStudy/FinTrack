package com.track.fin.record;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record DeleteAccountRecord(

        @NotNull
        @Min(1)
        Long userId,

        @NotBlank
        @Size(min = 10, max = 10)
        String accountNumber,

        String withdrawAccountNumber,

        LocalDateTime unRegisteredAt

) {
}
