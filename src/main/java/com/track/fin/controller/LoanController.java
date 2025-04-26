package com.track.fin.controller;

import com.track.fin.record.CreateLoanRecord;
import com.track.fin.record.LoanRecord;
import com.track.fin.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/loan")
    public LoanRecord createLoan(@RequestBody CreateLoanRecord createLoan) {
        return LoanRecord.from(loanService.createLoan(createLoan));
    }

    @GetMapping("/loan/{loanId}")
    public LoanRecord getLoan(@PathVariable Long loanId) {
        return LoanRecord.from(loanService.getLoan(loanId));
    }

    @PutMapping("/loan/{loanId}")
    public LoanRecord updateLoan(@PathVariable Long loanId, @RequestBody CreateLoanRecord updateLoan) {
        return LoanRecord.from(loanService.updateLoan(loanId, updateLoan));
    }

    @DeleteMapping("/loan/{loanId}")
    public void deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
    }

}
