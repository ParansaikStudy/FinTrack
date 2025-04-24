package com.track.fin.domain;

import com.track.fin.exception.AccountException;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import com.track.fin.type.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static com.track.fin.type.AccountStatus.LOCKED;
import static com.track.fin.type.AccountType.LOANS;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
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
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

    public void afterLoan() {
        accountType = LOANS;
        accountStatus = LOCKED;
    }

    // TODO: 사용자 생성 시 기본 회원 등급은 BRONZE

}
