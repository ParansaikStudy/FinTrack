package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.Loan;
import com.track.fin.domain.User;
import com.track.fin.record.CreateLoan;
import com.track.fin.repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final UserService userService;
    private final AccountService accountService;
    private final LoanRepository loanRepository;

    @Transactional
    public Loan createAccount(CreateLoan createLoan) {
        User user = userService.get(createLoan.userId());

        Account account = accountService.getAccount(createLoan.accountId());
        account.afterLoan();

        Loan loan = Loan.from(user, account, createLoan);
        return loanRepository.save(loan);
    }

}
