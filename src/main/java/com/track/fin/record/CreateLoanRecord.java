package com.track.fin.record;

import com.track.fin.type.LoanStatus;
import com.track.fin.type.LoanType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateLoanRecord(

        Long userId,
        Long accountId,
        Long balance,
        LocalDateTime LoanDate,
        LocalDate delinquencyDate,
        LoanStatus loanStatus,
        LoanType loanType,
        BigDecimal loanRate,
        BigDecimal collateralRate,
        BigDecimal discount

) {
}
