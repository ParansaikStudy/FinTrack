package com.track.fin.record;

import com.track.fin.domain.Loan;
import com.track.fin.type.LoanStatus;
import com.track.fin.type.LoanType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanRecord(
        Long id,
        Long userId,
        Long accountId,
        Long balance,
        LocalDateTime loanDate,
        LocalDate delinquencyDate,
        LoanStatus loanStatus,
        LoanType loanType
) {
    public static LoanRecord from(Loan loan) {
        return new LoanRecord(
                loan.getId(),
                loan.getUser().getId(),
                loan.getAccount().getId(),
                loan.getBalance(),
                loan.getLoanDate(),
                loan.getDelinquencyDate(),
                loan.getLoanStatus(),
                loan.getLoanType()
        );
    }
}
