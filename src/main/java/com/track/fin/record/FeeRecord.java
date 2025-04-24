package com.track.fin.record;

import com.track.fin.domain.Fee;
import com.track.fin.type.GradeType;

import java.math.BigDecimal;

public record FeeRecord(
        Long id,
        GradeType gradeType,
        BigDecimal collateralRate,
        BigDecimal interestRate,
        BigDecimal discount
) {
    public static FeeRecord from(Fee fee) {
        return new FeeRecord(
                fee.getId(),
                fee.getGradeType(),
                fee.getCollateralRate(),
                fee.getInterestRate(),
                fee.getDiscount()
        );
    }
}
