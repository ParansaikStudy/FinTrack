package com.track.fin.repository;

import com.track.fin.domain.Account;
import com.track.fin.domain.Loan;
import com.track.fin.type.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByAccountAndLoanStatusNot(Account account, LoanStatus loanStatus);
}
