package com.track.fin.dto;

import com.track.fin.type.AccountStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfo {

    private String accountNumber;

    private Long balance;

    private AccountStatus accountStatus;

    private LocalDateTime registeredAt;
}
