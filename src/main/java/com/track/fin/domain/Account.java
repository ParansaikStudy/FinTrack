package com.track.fin.domain;

import com.track.fin.exception.AccountException;
import com.track.fin.record.CreateAccount;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import jakarta.persistence.*;
import lombok.*;

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

    private Boolean autoTransfer;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private LocalDateTime unregisteredAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    // TODO: 대출 생성시 추가 예정
    private final Long lockedAmount = 0L;

    public void useBalance(Long amount) {
        if (accountStatus == LOCKED) {
            long availableBalance = balance - lockedAmount;

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
    private Account(Long id, User user, String accountNumber, Long balance, Boolean autoTransfer, AccountType accountType) {
        this.id = id;
        this.user = user;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.autoTransfer = autoTransfer;
        this.accountType = accountType;
        this.accountStatus = ACTIVE;
    }

    public static Account from(User user, CreateAccount createAccount, String newAccountNumber) {
        return Account.builder()
                .user(user)
                .accountNumber(newAccountNumber)
                .balance(createAccount.initialBalance())
                .accountType(createAccount.accountType())
                .build();
    }

}
