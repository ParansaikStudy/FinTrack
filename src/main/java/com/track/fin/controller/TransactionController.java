package com.track.fin.controller;

import com.track.fin.dto.CancelBalance;
import com.track.fin.dto.UseBalance;
import com.track.fin.exception.AccountException;
import com.track.fin.record.*;
import com.track.fin.service.TransactionService;
import com.track.fin.type.TransactionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/use")
    public TransferResponseRecord useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) {
        try {
            return transactionService.useBalance(
                    request.getUserId(),
                    request.getAccountNumber(),
                    request.getAmount(),
                    request.getTransactionMethodType()
            );
        } catch (AccountException e) {
            log.error("잔액 사용 실패: {}", e.getMessage());
            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount(),
                    request.getTransactionMethodType()
            );
            throw e;
        }
    }

    @PostMapping("/deposit")
    public DepositRecord deposit(
            @Valid @RequestBody DepositTransactionRecord request
    ) {
        try {
            return DepositRecord.from(
                    transactionService.deposit(
                            request.userId(),
                            request.accountNumber(),
                            request.amount(),
                            request.transactionMethodType()
                    )
            );
        } catch (AccountException e) {
            log.error("입금 실패: {}", e.getMessage());

            transactionService.saveFailedDepositTransaction(
                    request.accountNumber(),
                    request.amount(),
                    request.transactionMethodType()
            );
            throw e;
        }
    }

    @PostMapping("/transfer")
    public TransferResponseRecord transfer(
            @Valid @RequestBody TransferRequestRecord request
    ) {
        try {
            return transactionService.transfer(
                    request.userId(),
                    request.fromAccountNumber(),
                    request.toAccountNumber(),
                    request.amount(),
                    request.transactionMethodType()
            );
        } catch (AccountException e) {
            log.error("이체 실패: {}", e.getMessage());
            transactionService.saveFailedTransferTransaction(
                    request.fromAccountNumber(),
                    request.toAccountNumber(),
                    request.amount(),
                    request.transactionMethodType()
            );
            throw e;
        }
    }

    @GetMapping("/transfers")
    public List<TransferResponseRecord> getTransfers(
            @RequestParam String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(defaultValue = "false") boolean sortDesc
    ) {
        return transactionService.getTransferTransactionsByAccount(
                accountNumber, startDate, endDate, transactionType, sortDesc
        );
    }

    @PostMapping("/withdraw")
    public WithdrawalRecord withdraw(
            @Valid @RequestBody WithdrawalRequestRecord request
    ) {
        try {
            return WithdrawalRecord.from(
                    transactionService.withdraw(
                            request.userId(),
                            request.accountNumber(),
                            request.amount(),
                            request.transactionMethodType()
                    )
            );
        } catch (AccountException e) {
            log.error("출금 실패: {}", e.getMessage());
            transactionService.saveFailedWithdrawTransaction(
                    request.accountNumber(),
                    request.amount(),
                    request.transactionMethodType()
            );
            throw e;
        }
    }

    @PostMapping("/cancel")
    public TransferResponseRecord cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {
        try {
            return transactionService.cancelBalance(
                    String.valueOf(request.getTransactionId()),
                    request.getAccountNumber(),
                    request.getAmount(),
                    request.getTransactionMethodType()
            );
        } catch (AccountException e) {
            log.error("Cancel failed", e);
            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount(),
                    request.getTransactionMethodType()
            );
            throw e;
        }
    }

    @GetMapping("/{transactionId}")
    public TransferResponseRecord queryTransaction(
            @PathVariable String transactionId) {
        return transactionService.queryTransactionId(transactionId);
    }

}
