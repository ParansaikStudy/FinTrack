package com.track.fin.type;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum GradeType {

    BRONZE(new BigDecimal("90"), new BigDecimal("0.05"), new BigDecimal("0.01")),
    SILVER(new BigDecimal("80"), new BigDecimal("0.04"), new BigDecimal("0.02")),
    GOLD(new BigDecimal("70"), new BigDecimal("0.03"), new BigDecimal("0.03")),
    PREMIUM(new BigDecimal("60"), new BigDecimal("0.02"), new BigDecimal("0.04")),
    DIAMOND(new BigDecimal("50"), new BigDecimal("0.01"), new BigDecimal("0.05")),
    ;

    @Column(precision = 10, scale = 2)
    private final BigDecimal collateralRate;

    @Column(precision = 10, scale = 2)
    private final BigDecimal interestRate;

    @Column(precision = 10, scale = 2)
    private final BigDecimal discount;

}
