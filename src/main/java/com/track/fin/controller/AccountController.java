package com.track.fin.controller;

import com.track.fin.domain.Account;
import com.track.fin.dto.AccountDto;
import com.track.fin.dto.AccountInfo;
import com.track.fin.dto.CreateAccount;
import com.track.fin.dto.DeleteAccount;
import com.track.fin.record.AccountRecord;
import com.track.fin.record.TransferResponseRecord;
import com.track.fin.service.AccountService;
import com.track.fin.service.TransactionService;
import com.track.fin.type.AccountType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping("/accounts")
    public AccountRecord createAccount(
            @RequestParam Long userId,
            @RequestParam Long initialBalance,
            @RequestParam AccountType accountType
    ) {
        return accountService.createAccount(userId, initialBalance, accountType);
    }

    @GetMapping("/accounts")
    public List<AccountRecord> getAccountsByUserId(
            @RequestParam("userId") Long userId
    ) {
        return accountService.getAccounts(userId).stream()
                .map(AccountRecord::from)
                .toList();
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(
            @PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @DeleteMapping("/accounts")
    public AccountRecord deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return accountService.deleteAccount(
                request.getUserId(),
                request.getAccountNumber(),
                request.getWithdrawAccountNumber()
        );
    }

    @GetMapping("accounts/{accountNumber}/transactions")
    public List<TransferResponseRecord> getTransferTransactions(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "true") boolean sortDesc
    ) {
        return transactionService.getTransferTransactionsByAccount(
                accountNumber,
                startDate,
                endDate,
                sortDesc
        );
    }

    @GetMapping("/accounts/active")
    public List<AccountRecord> getActiveAccounts(
            @RequestParam("userId") Long userId
    ) {
        return accountService.getActiveAccounts(userId).stream()
                .map(AccountRecord::from)
                .toList();
    }

    @GetMapping("accounts/{accountId}/collateral")
    public BigDecimal getCollateralRate(
            @PathVariable Long accountId,
            @RequestParam("userId") Long userId
    ) {
        return accountService.getAccountCollateralRate(userId, accountId);
    }

}
