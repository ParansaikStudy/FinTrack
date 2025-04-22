package com.track.fin.dto;

import com.track.fin.type.AccountType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class CreateAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        private AccountType accountType;

        @NotNull
        @Min(0)
        private Long initialBalance;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long userId;

        private String accountNumber;

        public static Response from(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .build();
        }
    }

}
