package com.track.fin.controller;

import com.track.fin.dto.CancelBalance;
import com.track.fin.dto.QueryTransactionResponse;
import com.track.fin.dto.UseBalance;
import com.track.fin.exception.AccountException;
import com.track.fin.record.*;
import com.track.fin.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("transaction/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request requset
    ) {
        try {
            return UseBalance.Response.from(transactionService.useBalance(requset.getUserId(),
                    requset.getAccountNumber(), requset.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance. ");

            transactionService.saveFailedUseTransaction(
                    requset.getAccountNumber(),
                    requset.getAmount()
            );
            throw e;
        }
    }

    @PostMapping("/transaction/deposit")
    public DepositRecord deposit(
            @Valid @RequestBody DepositTransactionRecord request
    ) {
        try {
            return DepositRecord.from(
                    transactionService.deposit(
                            request.userId(),
                            request.accountNumber(),
                            request.amount()
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to deposit.");

            transactionService.saveFailedDepositTransaction(
                    request.accountNumber(),
                    request.amount()
            );
            throw e;
        }
    }

    @PostMapping("/transaction/transfer")
    public TransferResponseRecord transfer(
            @Valid @RequestBody TransferRequestRecord request
    ) {
        try {
            return transactionService.transfer(
                    request.userId(),
                    request.fromAccountNumber(),
                    request.toAccountNumber(),
                    request.amount()
            );
        } catch (AccountException e) {
            log.error("Failed to transfer.");
            transactionService.saveFailedTransferTransaction(
                    request.fromAccountNumber(),
                    request.toAccountNumber(),
                    request.amount()
            );
            throw e;
        }
    }


    @PostMapping("/transaction/withdraw")
    public WithdrawalRecord withdraw(
            @Valid @RequestBody WithdrawalRequestRecord request
    ) {
        try {
            return WithdrawalRecord.from(
                    transactionService.withdraw(
                            request.userId(),
                            request.accountNumber(),
                            request.amount()
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to withdraw.");
            transactionService.saveFailedWithdrawTransaction(request.accountNumber(), request.amount());
            throw e;
        }
    }

    @PostMapping("transaction/cancel")
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(String.valueOf(request.getTransactionId()),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance. ");

            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId) {
        return QueryTransactionResponse.from(transactionService.queryTransactionId(transactionId));
    }
}
