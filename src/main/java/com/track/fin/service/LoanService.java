package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.Loan;
import com.track.fin.domain.User;
import com.track.fin.exception.AccountException;
import com.track.fin.record.CreateLoan;
import com.track.fin.repository.LoanRepository;
import com.track.fin.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final UserService userService;
    private final AccountService accountService;
    private final LoanRepository loanRepository;

    @Transactional
    public Loan createLoan(CreateLoan createLoan) {
        User user = userService.get(createLoan.userId());

        Account account = accountService.getAccount(createLoan.accountId());
        account.afterLoan();

        Loan loan = Loan.from(user, account, createLoan);
        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public Loan getLoan(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new AccountException(ErrorCode.LOAN_NOT_FOUND));
    }

    @Transactional
    public Loan updateLoan(Long loanId, CreateLoan updateLoan) {
        Loan loan = getLoan(loanId);

        Account account = accountService.getAccount(updateLoan.accountId());
        User user = userService.get(updateLoan.userId());

       loan.update(user, account, updateLoan);

        return loan;
    }

    @Transactional
    public void deleteLoan(Long loanId) {
        Loan loan = getLoan(loanId);
        loanRepository.delete(loan);
    }

}
