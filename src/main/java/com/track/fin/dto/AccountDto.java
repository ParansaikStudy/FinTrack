package com.track.fin.dto;

import com.track.fin.type.AccountType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 삭제 예정
public class AccountDto {

    private Long userId;

    private String accountNumber;

    private Long balance;

    private AccountType accountType;
}
