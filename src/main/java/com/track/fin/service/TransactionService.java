package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.Transaction;
import com.track.fin.domain.User;
import com.track.fin.dto.TransactionDto;
import com.track.fin.exception.AccountException;
import com.track.fin.record.TransferResponseRecord;
import com.track.fin.repository.TransactionRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static com.track.fin.type.ErrorCode.*;
import static com.track.fin.type.TransactionResultType.FAIL;
import static com.track.fin.type.TransactionResultType.SUCCESS;
import static com.track.fin.type.TransactionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountService accountService;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        User user = userService.get(userId);
        Account account = accountService.getAccountByNumber(accountNumber);

        validateUserBalance(user, account, amount);
        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount));
    }

    private Transaction saveAndGetTransaction(TransactionType type, TransactionResultType result, Account account, Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .id(UUID.randomUUID().toString().replace("-", ""))
                        .transactionType(type)
                        .transactionResultType(result)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .build()
        );
    }

    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId, String accountNumber, Long amount) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));
        Account account = accountService.getAccountByNumber(accountNumber);

        validateCancelBalance(transaction, account, amount);
        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount));
    }

    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
    }

    public TransactionDto queryTransactionId(String transactionId) {
        return TransactionDto.fromEntity(
                transactionRepository.findById(transactionId)
                        .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND))
        );
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(TRANSACTION_ACCOUNT_UN_MATCH);
        }
        if (!Objects.equals(transaction.getAmount(), amount)) {
            throw new AccountException(CANCEL_MUST_FULLY);
        }
    }

    private void validateUserBalance(User user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public Transaction deposit(Long userId, String accountNumber, Long amount) {
        User user = userService.get(userId);
        Account account = accountService.getAccountByNumber(accountNumber);

        validateDeposit(user, account);
        account.deposit(amount);

        return saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount);
    }

    public void saveFailedDepositTransaction(String accountNumber, Long amount) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
    }

    private void validateDeposit(User user, Account account) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
    }

    @Transactional
    public Transaction withdraw(Long userId, String accountNumber, Long amount) {
        User user = userService.get(userId);
        Account account = accountService.getAccountByNumber(accountNumber);

        validateWithdraw(user, account, amount);
        account.withdraw(amount);

        return saveAndGetTransaction(WITHDRAWAL, SUCCESS, account, amount);
    }

    public void saveFailedWithdrawTransaction(String accountNumber, Long amount) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(WITHDRAWAL, FAIL, account, amount);
    }

    private void validateWithdraw(User user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public TransferResponseRecord transfer(Long userId, String fromAccountNumber, String toAccountNumber, Long amount) {
        User user = userService.get(userId);
        Account fromAccount = accountService.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountService.getAccountByNumber(toAccountNumber);

        validateTransfer(user, fromAccount, toAccount, amount);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction fromTransaction = saveAndGetTransaction(TRANSFER, SUCCESS, fromAccount, amount);
        Transaction toTransaction = saveAndGetTransaction(TRANSFER, SUCCESS, toAccount, amount);

        return TransferResponseRecord.from(fromTransaction, toTransaction);
    }

    public void saveFailedTransferTransaction(String fromAccountNumber, String toAccountNumber, Long amount) {
        Account fromAccount = accountService.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountService.getAccountByNumber(toAccountNumber);
        saveAndGetTransaction(TRANSFER, FAIL, fromAccount, amount);
        saveAndGetTransaction(TRANSFER, FAIL, toAccount, amount);
    }

    private void validateTransfer(User user, Account from, Account to, Long amount) {
        if (!Objects.equals(user.getId(), from.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (from.getAccountStatus() != AccountStatus.ACTIVE || to.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (from.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new AccountException(INVALID_REQUEST);
        }
    }

}
