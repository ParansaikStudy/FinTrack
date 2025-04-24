package com.track.fin.controller;

import com.track.fin.domain.Account;
import com.track.fin.dto.AccountDto;
import com.track.fin.dto.AccountInfo;
import com.track.fin.dto.CreateAccount;
import com.track.fin.dto.DeleteAccount;
import com.track.fin.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

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
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountsByuserId(userId)
                .stream().map(accountDto ->
                        AccountInfo.builder()
                                .accountNumber(accountDto.getAccountNumber())
                                .balance(accountDto.getBalance())
                                .accountStatus(accountDto.getAccountStatus())
                                .registeredAt(accountDto.getRegisteredAt())
                                .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
