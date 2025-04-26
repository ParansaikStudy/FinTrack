package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.Transaction;
import com.track.fin.domain.User;
import com.track.fin.dto.TransactionDto;
import com.track.fin.exception.AccountException;
import com.track.fin.record.TransferResponse;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.TransactionRepository;
import com.track.fin.repository.UserRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.ErrorCode;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.track.fin.type.TransactionResultType.FAIL;
import static com.track.fin.type.TransactionResultType.SUCCESS;
import static com.track.fin.type.TransactionType.DEPOSIT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountService accountService;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        validateUserBalance(user, account, amount);
        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount));
    }

    private Transaction saveAndGetTransaction(TransactionType transactionType,
                                              TransactionResultType transactionResultType,
                                              Account account,
                                              Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .id(UUID.randomUUID().toString().replace("-", ""))
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .build()
        );
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId,
                                        String accountNumber,
                                        Long amount) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(transaction, account, amount);
        account.useBalance(amount);

        return TransactionDto.fromEntity(saveAndGetTransaction(DEPOSIT, SUCCESS, account, amount));
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(DEPOSIT, FAIL, account, amount);
    }

    public TransactionDto queryTransactionId(String transactionId) {
        return TransactionDto.fromEntity(transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }

    private void validateCancelBalance(Transaction transaction, Account account, Long amount) {
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);
        }
        if (!Objects.equals(transaction.getAmount(), amount)) {
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }
//        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
//            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
//        }
    }

    private void validateUserBalance(User user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
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

    @Transactional
    public void saveFailedDepositTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionType.DEPOSIT, FAIL, account, amount);
    }

    private void validateDeposit(User user, Account account) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
    }

    @Transactional
    public Transaction withdraw(Long userId, String accountNumber, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateWithdraw(user, account, amount);

        account.withdraw(amount);

        return saveAndGetTransaction(TransactionType.WITHDRAWAL, SUCCESS, account, amount);
    }

    @Transactional
    public void saveFailedWithdrawTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(TransactionType.WITHDRAWAL, FAIL, account, amount);
    }

    private void validateWithdraw(User user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public TransferResponse transfer(Long userId, String fromAccountNumber, String toAccountNumber, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateTransfer(user, fromAccount, toAccount, amount);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction fromTransaction = saveAndGetTransaction(TransactionType.TRANSFER, SUCCESS, fromAccount, amount);
        Transaction toTransaction = saveAndGetTransaction(TransactionType.TRANSFER, SUCCESS, toAccount, amount);

        return TransferResponse.from(fromTransaction, toTransaction);
    }

    public void saveFailedTransferTransaction(String fromAccountNumber, String toAccountNumber, Long amount) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(TransactionType.TRANSFER, FAIL, fromAccount, amount);
        saveAndGetTransaction(TransactionType.TRANSFER, FAIL, toAccount, amount);
    }

    private void validateTransfer(User user, Account from, Account to, Long amount) {
        if (!Objects.equals(user.getId(), from.getUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if (from.getAccountStatus() != AccountStatus.ACTIVE || to.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (from.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
    }

}
