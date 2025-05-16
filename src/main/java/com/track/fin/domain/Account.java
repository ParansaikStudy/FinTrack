package com.track.fin.domain;

import com.track.fin.exception.AccountException;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.track.fin.type.AccountStatus.LOCKED;
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

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    public void useBalance(Long amount) {
        if (accountStatus == LOCKED) {
            // TODO: 담보 받은 금액 외에 사용 가능
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
