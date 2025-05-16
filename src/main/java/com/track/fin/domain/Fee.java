package com.track.fin.domain;

import com.track.fin.type.GradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GradeType gradeType;

    @Column(precision = 10, scale = 2)
    private BigDecimal collateralRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal interestRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount;

    @Builder
    private Fee(Long id, GradeType gradeType, BigDecimal collateralRate, BigDecimal interestRate, BigDecimal discount) {
        this.id = id;
        this.gradeType = gradeType;
        this.collateralRate = collateralRate;
        this.interestRate = interestRate;
        this.discount = discount;
    }

    public static Fee fromGradeType(GradeType gradeType) {
        return Fee.builder()
                .gradeType(gradeType)
                .collateralRate(gradeType.getCollateralRate())
                .interestRate(gradeType.getInterestRate())
                .discount(gradeType.getDiscount())
                .build();
    }

    public void updateGrade(GradeType newGrade) {
        this.gradeType = newGrade;
        this.collateralRate = newGrade.getCollateralRate();
        this.interestRate = newGrade.getInterestRate();
        this.discount = newGrade.getDiscount();
    }

}
