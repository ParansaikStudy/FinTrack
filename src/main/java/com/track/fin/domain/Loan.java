package com.track.fin.domain;

import com.track.fin.type.LoanStstus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Loan {

    @Id
    @GeneratedValue
    private Long id;

    private String collateralInfo;

    private Long amount;

    private BigDecimal ratio;

    @Enumerated(EnumType.STRING)
    private LoanStstus status;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime unregisteredAt;

}
