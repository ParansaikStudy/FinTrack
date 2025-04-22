package com.track.fin.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {

    DEPOSIT(1000L, 0.1),
    SAVINGS(10000L, 0.2),
    LOANS(0L, 0.3),
    ;

    private Long balance;
    private Double rate;

}
