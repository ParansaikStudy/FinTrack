package com.track.fin.domain;

import com.track.fin.type.GradeType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Grade {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private GradeType grade;

    private Long monthlyBalance;

    private Long totalTransaction;

    private Long amount;

    private DecimalFormat discount;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime unregisteredAt;

}
