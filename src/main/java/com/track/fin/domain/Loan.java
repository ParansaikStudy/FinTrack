package com.track.fin.domain;

import com.track.fin.record.CreateLoan;
import com.track.fin.type.LoanStatus;
import com.track.fin.type.LoanType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToOne
    private Account account;

    // 대출 금액
    private Long balance;

    // 대출 일
    private LocalDateTime loanDate;

    // 상환 일
    private LocalDate delinquencyDate;

    // 상환 일
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Builder
    private Loan(Long id, User user, Account account, Long balance, LocalDateTime loanDate, LocalDate delinquencyDate, LoanStatus loanStatus, LoanType loanType) {
        this.id = id;
        this.user = user;
        this.account = account;
        this.balance = balance;
        this.loanDate = loanDate;
        this.delinquencyDate = delinquencyDate;
        this.loanStatus = loanStatus;
        this.loanType = loanType;
    }

    public static Loan from(User user, Account account, CreateLoan createLoan) {
        return Loan.builder()
                .user(user)
                .account(account)
                .balance(createLoan.balance())
                .loanDate(LocalDateTime.now())
                .delinquencyDate(createLoan.delinquencyDate())
                .loanStatus(createLoan.loanStatus())
                .loanType(createLoan.loanType())
                .build();
    }

}
