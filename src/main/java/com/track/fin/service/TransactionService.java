package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.Transaction;
import com.track.fin.domain.User;
import com.track.fin.exception.AccountException;
import com.track.fin.record.TransferResponseRecord;
import com.track.fin.repository.TransactionRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.TransactionMethodType;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private static final long MAX_DEPOSIT_AMOUNT = 1_000_000L;

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;
    private final UserService userService;

    @Transactional
    public TransferResponseRecord useBalance(Long userId, String accountNumber, Long amount, TransactionMethodType methodType) {
        User user = userService.get(userId);

        Account account = accountService.getAccountByNumber(accountNumber);

        validateUserBalance(user, account, amount);
        account.useBalance(amount);

        Transaction transaction = saveAndGetTransaction(WITHDRAWAL, SUCCESS, account, amount, methodType);

        return TransferResponseRecord.from(transaction);
    }

    private Transaction saveAndGetTransaction(
            TransactionType type,
            TransactionResultType result,
            Account account,
            Long amount,
            TransactionMethodType methodType
    ) {
        return transactionRepository.save(
                Transaction.builder()
                        .id(UUID.randomUUID().toString().replace("-", ""))
                        .transactionType(type)
                        .transactionResultType(result)
                        .transactionMethodType(methodType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<TransferResponseRecord> getTransferTransactionsByAccount(
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate,
            TransactionType transactionType,
            boolean sortDesc
    ) {
        Account account = getAccountByNumber(accountNumber);
        List<Transaction> transfers = getTransferTransactions(account, startDate, endDate, transactionType);
        List<TransferResponseRecord> results = convertToTransferRecords(transfers, accountNumber);
        sortTransferRecords(results, sortDesc);
        return results;
    }

    private Account getAccountByNumber(String accountNumber) {
        return accountService.getAccountByNumber(accountNumber);
    }

    private List<Transaction> getTransferTransactions(Account account, LocalDateTime start, LocalDateTime end, TransactionType type) {
        if (type == null) {
            return transactionRepository.findByAccountAndTransactionDateBetween(account, start, end);
        }
        return transactionRepository.findByAccountAndTransactionTypeAndTransactionDateBetween(account, type, start, end);
    }

    private List<TransferResponseRecord> convertToTransferRecords(List<Transaction> transactions, String accountNumber) {
        List<TransferResponseRecord> result = new ArrayList<>();

        for (int i = 0; i < transactions.size() - 1; i++) {
            Transaction t1 = transactions.get(i);
            Transaction t2 = transactions.get(i + 1);

            if (isValidTransferPair(t1, t2)) {
                Transaction from = t1.getAccount().getAccountNumber().equals(accountNumber) ? t1 : t2;
                Transaction to = from == t1 ? t2 : t1;

                result.add(TransferResponseRecord.from(from, to));
                i++;
            }
        }

        return result;
    }

    private boolean isValidTransferPair(Transaction t1, Transaction t2) {
        return !t1.getAccount().getAccountNumber().equals(t2.getAccount().getAccountNumber()) &&
                Objects.equals(t1.getAmount(), t2.getAmount());
    }

    private void sortTransferRecords(List<TransferResponseRecord> list, boolean sortDesc) {
        list.sort((a, b) -> sortDesc
                ? b.transactionDate().compareTo(a.transactionDate())
                : a.transactionDate().compareTo(b.transactionDate()));
    }

    public void saveFailedUseTransaction(String accountNumber, Long amount, TransactionMethodType transactionMethodType) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount, transactionMethodType);
    }

    @Transactional
    public TransferResponseRecord cancelBalance(
            String transactionId,
            String accountNumber,
            Long amount,
            TransactionMethodType methodType
    ) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));

        Account account = accountService.getAccountByNumber(accountNumber);

        validateCancelBalance(transaction, account, amount);
        account.useBalance(amount);

        Transaction newTransaction = saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount, methodType);
        return TransferResponseRecord.from(newTransaction);
    }

    public void saveFailedCancelTransaction(String accountNumber, Long amount, TransactionMethodType methodType) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount, methodType);
    }

    public TransferResponseRecord queryTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));
        return TransferResponseRecord.from(transaction);
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
    public Transaction deposit(Long userId, String accountNumber, Long amount, TransactionMethodType methodType) {
        User user = userService.get(userId);

        Account account = accountService.getAccountByNumber(accountNumber);

        validateDeposit(user, account, amount);

        account.deposit(amount);

        return saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount, methodType);
    }

    public void saveFailedDepositTransaction(String accountNumber, Long amount, TransactionMethodType methodType) {
        Account account = accountService.getAccountByNumber(accountNumber);
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount, methodType);
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
    public Transaction withdraw(Long userId, String accountNumber, Long amount, TransactionMethodType methodType) {
        User user = userService.get(userId);

        Account account = accountService.getAccountByNumber(accountNumber);

        validateWithdraw(user, account, amount);
        account.withdraw(amount);

        return saveAndGetTransaction(WITHDRAWAL, SUCCESS, account, amount, methodType);
    }

    public void saveFailedWithdrawTransaction(String accountNumber, Long amount, TransactionMethodType methodType) {
        try {
            Account account = accountService.getAccountByNumber(accountNumber);
            saveAndGetTransaction(WITHDRAWAL, FAIL, account, amount, methodType);
        } catch (AccountException e) {
            log.error("출금 실패 거래 내역 저장 중 오류 발생 - 계좌번호: {}", accountNumber, e);
            throw e;
        }
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
    public TransferResponseRecord transfer(Long userId, String fromAccountNumber, String toAccountNumber, Long amount, TransactionMethodType methodType) {
        User user = userService.get(userId);
        Account fromAccount = accountService.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountService.getAccountByNumber(toAccountNumber);

        validateTransfer(user, fromAccount, toAccount, amount);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction fromTransaction = saveAndGetTransaction(TRANSFER, SUCCESS, fromAccount, amount, methodType);
        Transaction toTransaction = saveAndGetTransaction(TRANSFER, SUCCESS, toAccount, amount, methodType);

        return TransferResponseRecord.from(fromTransaction, toTransaction);
    }

    public void saveFailedTransferTransaction(String fromAccountNumber, String toAccountNumber, Long amount, TransactionMethodType methodType) {
        Account fromAccount = accountService.getAccountByNumber(fromAccountNumber);
        Account toAccount = accountService.getAccountByNumber(toAccountNumber);

        saveAndGetTransaction(TRANSFER, FAIL, fromAccount, amount, methodType);
        saveAndGetTransaction(TRANSFER, FAIL, toAccount, amount, methodType);
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

    private void validateDeposit(User user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (amount > MAX_DEPOSIT_AMOUNT) {
            throw new AccountException(AMOUNT_EXCEED_DEPOSIT_LIMIT);
        }
    }

    public boolean existsByAccountAndTransactionMethodType(Account account, TransactionMethodType auto) {
        return transactionRepository.existsByAccountAndTransactionMethodType(account, auto);
    }

}
