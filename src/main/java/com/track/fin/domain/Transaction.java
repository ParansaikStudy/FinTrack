package com.track.fin.domain;

import com.track.fin.type.TransactionMethodType;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    @Enumerated(EnumType.STRING)
    private TransactionMethodType transactionMethodType;

    @CreatedDate
    private LocalDateTime transactionDate;

    private Long amount;

    private Long balanceSnapshot;

    private Long fee;

    private String memo;

}
