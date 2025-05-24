package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.Transaction;
import com.track.fin.domain.User;
import com.track.fin.dto.TransactionDto;
import com.track.fin.exception.AccountException;
import com.track.fin.record.TransferResponseRecord;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.TransactionRepository;
import com.track.fin.repository.UserRepository;
import com.track.fin.type.AccountStatus;
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

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateUserBalance(user, account, amount);
        account.useBalance(amount);

        return TransactionDto.fromEntity(
                saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount)
        );
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

    @Transactional(readOnly = true)
    public List<TransferResponseRecord> getTransferTransactionsByAccount(
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean sortDesc
    ) {
        Account account = getAccountByNumber(accountNumber);
        List<Transaction> transfers = getTransferTransactions(account, startDate, endDate);
        List<TransferResponseRecord> results = convertToTransferRecords(transfers, accountNumber);
        sortTransferRecords(results, sortDesc);
        return results;
    }

    private Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
    }

    private List<Transaction> getTransferTransactions(Account account, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByAccountAndTransactionTypeAndTransactionDateBetween(
                account, TransactionType.TRANSFER, start, end
        );
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

    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
    }

    @Transactional
    public TransferResponseRecord cancelBalance(String transactionId, String accountNumber, Long amount) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateCancelBalance(transaction, account, amount);
        account.useBalance(amount);

        Transaction newTransaction = saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount);
        return TransferResponseRecord.from(newTransaction);
    }

    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
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
    public Transaction deposit(Long userId, String accountNumber, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeposit(user, account);
        account.deposit(amount);

        return saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount);
    }

    public void saveFailedDepositTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateWithdraw(user, account, amount);
        account.withdraw(amount);

        return saveAndGetTransaction(WITHDRAWAL, SUCCESS, account, amount);
    }

    public void saveFailedWithdrawTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateTransfer(user, fromAccount, toAccount, amount);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction fromTransaction = saveAndGetTransaction(TRANSFER, SUCCESS, fromAccount, amount);
        Transaction toTransaction = saveAndGetTransaction(TRANSFER, SUCCESS, toAccount, amount);

        return TransferResponseRecord.from(fromTransaction, toTransaction);
    }

    public void saveFailedTransferTransaction(String fromAccountNumber, String toAccountNumber, Long amount) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
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
