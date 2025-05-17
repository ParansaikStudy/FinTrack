package com.track.fin.domain;

import com.track.fin.type.TransactionMethodType;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    private Transaction(String id, Account account, TransactionType transactionType, TransactionResultType transactionResultType, TransactionMethodType transactionMethodType, LocalDateTime transactionDate, Long amount, Long balanceSnapshot, Long fee, String memo) {
        this.id = id;
        this.account = account;
        this.transactionType = transactionType;
        this.transactionResultType = transactionResultType;
        this.transactionMethodType = transactionMethodType;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.balanceSnapshot = balanceSnapshot;
        this.fee = fee;
        this.memo = memo;
    }
    
}
