package com.track.fin.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {

    DEPOSIT("적금", 1000L, 0.1),
    SAVINGS("예금", 10000L, 0.2),
    LOANS("대출", 0L, 0.3),
    ;

    private String name;
    private Long minimumBalance;
    private Double rate;

}
