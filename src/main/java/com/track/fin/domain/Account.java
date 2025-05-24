package com.track.fin.domain;

import com.track.fin.exception.AccountException;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

import static com.track.fin.type.AccountStatus.*;
import static com.track.fin.type.AccountType.LOANS;
import static com.track.fin.type.ErrorCode.AMOUNT_EXCEED_BALANCE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private Long balance;

    private Long minBalance;

    private Boolean autoTransfer;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private LocalDateTime unregisteredAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private Long lockedAmount = 0L;

    public void useBalance(Long amount) {
        if (accountStatus == LOCKED) {
            Long availableBalance = balance - lockedAmount;

            if (availableBalance < amount) {
                throw new AccountException(AMOUNT_EXCEED_BALANCE);
            }
            balance -= amount;
            return;
        }
        if (amount > balance) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void afterLoan() {
        accountType = LOANS;
        accountStatus = LOCKED;
        // 대출 생성시 추가 예정
//       lockedAmount = collateralAmount;
    }

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void withdraw(Long amount) {
        if (this.balance < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
        this.balance -= amount;
    }

    public void close() {
        this.accountStatus = CLOSED;
        this.unregisteredAt = LocalDateTime.now();
    }

    public void restore() {
        this.accountStatus = ACTIVE;
        this.unregisteredAt = null;
    }

    @Builder
    private Account(Long id, User user, String accountNumber, Long balance, Long minBalance, Boolean autoTransfer, AccountType accountType, AccountStatus accountStatus) {
        this.id = id;
        this.user = user;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.minBalance = minBalance;
        this.autoTransfer = autoTransfer;
        this.accountType = accountType;
        this.accountStatus = accountStatus;

    }
}