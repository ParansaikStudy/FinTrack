package com.track.fin.controller;

import com.track.fin.domain.Loan;
import com.track.fin.record.CreateLoan;
import com.track.fin.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/loan")
    public Loan createAccount(@RequestBody CreateLoan createLoan) {
        return loanService.createAccount(createLoan);
    }

}
