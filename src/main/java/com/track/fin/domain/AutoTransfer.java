package com.track.fin.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AutoTransfer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Account withdrawalAccount;

    @ManyToOne
    private Account depositAccount;

    private Long amount;

    private String schedule;

    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;
}
