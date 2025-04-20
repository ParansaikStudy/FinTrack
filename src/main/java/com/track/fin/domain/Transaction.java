package com.track.fin.domain;

import com.track.fin.type.TransactionMethodType;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String account;

    @CreatedDate
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionMethodType transactionMethodType;

    private Long amount;

    private Long fee;

    private String memeo;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime unregisteredAt;

}
