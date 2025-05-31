package com.track.fin.controller;

import com.track.fin.domain.Account;
import com.track.fin.record.*;
import com.track.fin.service.AccountFacadeService;
import com.track.fin.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountFacadeService accountFacadeService;

    @PostMapping
    public Account createAccount(
            CreateAccount createAccount
    ) {
        return accountService.createAccount(createAccount);
    }

    @GetMapping
    public List<AccountRecord> getAccountsByUserId(
            @RequestParam("userId") Long userId
    ) {
        return accountService.getAccounts(userId).stream()
                .map(AccountRecord::from)
                .toList();
    }

    @GetMapping("/{id}")
    public Account getAccount(
            @PathVariable Long id
    ) {
        return accountService.getAccount(id);
    }

    @PostMapping("/restore")
    public AccountRecord restoreAccount(
            @RequestParam Long userId,
            @RequestParam String accountNumber
    ) {
        return accountFacadeService.restoreAccount(userId, accountNumber);
    }

    @GetMapping("/{accountNumber}/transactions")
    public TransferResponseRecord getTransferTransactions(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "true") boolean sortDesc,
            @RequestBody TransferRequestRecord transferRequestRecord
    ) {
        return TransferResponseRecord.from(null);
    }

    @GetMapping("/active")
    public List<AccountRecord> getActiveAccounts(
            @RequestParam("userId") Long userId
    ) {
        return accountService.getActiveAccounts(userId).stream()
                .map(AccountRecord::from)
                .toList();
    }

    @GetMapping("/{accountId}/collateral")
    public BigDecimal getCollateralRate(
            @PathVariable Long accountId,
            @RequestParam("userId") Long userId
    ) {
        return accountService.getAccountCollateralRate(userId, accountId);
    }

    @PostMapping("/{accountNumber}/restore")
    public CreateAccountRecord restoreAccount(
            @PathVariable String accountNumber,
            @RequestParam Long userId
    ) {
        return CreateAccountRecord.from(accountService.restoreAccount(userId, accountNumber));
    }

    @GetMapping("/{accountNumber}/auto-transfer")
    public boolean isAutoTransferRegistered(
            @PathVariable String accountNumber
    ) {
        return accountService.isAutoTransferRegistered(accountNumber);
    }

    @GetMapping("/{accountNumber}/auto-transfer/validate")
    public boolean validateAutoTransferNotRegistered(
            @PathVariable String accountNumber
    ) {
        return accountService.validateAutoTransferNotRegistered(accountNumber);
    }

    @DeleteMapping
    public AccountRecord deleteAccount(@Valid @RequestBody DeleteAccountRecord request) {
        return accountFacadeService.deleteAccount(request);
    }

    @DeleteMapping("/expired/{accountNumber}")
    public void deleteExpiredAccount(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        accountFacadeService.deleteIfExpired(account);
    }

}
