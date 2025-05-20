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

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {
        AccountDto accountDto = accountService.createAccount(
                request.getUserId(),
                request.getInitialBalance(),
                request.getAccountType()
        );
        return CreateAccount.Response.from(accountDto);
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber(),
                        request.getWithdrawAccountNumber()
                )
        );
    }

    @GetMapping("/accounts")
    public List<AccountRecord> getAccountsByUserId(
            @RequestParam("userId") Long userId
    ) {
        return accountService.getAccounts(userId).stream()
                .map(account -> new AccountRecord(
                        account.getUser().getId(),
                        account.getAccountNumber(),
                        account.getBalance(),
                        account.getAccountType(),
                        account.getRegisterdAt(),
                        account.getUnregisteredAt()
                ))
                .collect(Collectors.toList());
    }


    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {
        return accountService.getAccount(id);
    }



    @GetMapping("/{accountNumber}/transactions")
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
    public List<AccountInfo> getActiveAccounts(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getActiveAccounts(userId).stream()
                .map(account -> AccountInfo.builder()
                        .accountNumber(account.getAccountNumber())
                        .balance(account.getBalance())
                        .accountStatus(account.getAccountStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/{accountId}/collateral")
    public BigDecimal getCollateralRate(
            @PathVariable Long accountId,
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountCollateralRate(userId, accountId);
    }

}
