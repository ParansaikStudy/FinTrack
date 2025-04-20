package com.track.fin.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class Fee {

    @Id
    @GeneratedValue
    private Long id;

    private String policyName;

    private BigDecimal discountRate;

    private Long limit;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime unregisteredAt;

}
