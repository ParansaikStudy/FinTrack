package com.track.fin.domain;

import com.track.fin.type.GradeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
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
}
