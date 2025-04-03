package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.AccountUser;
import com.track.fin.domain.Transaction;
import com.track.fin.dto.TransactionDto;
import com.track.fin.exception.AccountException;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.AccountUserRepository;
import com.track.fin.repository.TransactionRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.ErrorCode;
import com.track.fin.type.TransactionResultType;
import com.track.fin.type.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.track.fin.type.TransactionResultType.F;
import static com.track.fin.type.TransactionResultType.S;
import static com.track.fin.type.TransactionType.CANCEL;
import static com.track.fin.type.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository tansactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {

        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                        .orElseThrow(() ->new AccountException(ErrorCode.USER_NOT_FOUND));

        validateUserBalance(user, account, amount);

        account.useBalance(amount);


        return TransactionDto.fromEntity(saveAndGetTransaction(USE, S, account, amount));

    }

    private Transaction saveAndGetTransaction(TransactionType transactionType,
                                              TransactionResultType transactionResultType,
                                              Account account,
                                              Long amount) {
        return tansactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    private void validateUserBalance(AccountUser user, Account account, Long amount) {
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if(account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }
    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(USE,F, account, amount);


    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId,
                                        String accountNumber,
                                        Long amount
    ) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validatCancelBalance(transaction, account,amount);
        account.useBalance(amount);

        return TransactionDto.fromEntity(
                saveAndGetTransaction(CANCEL,S,account,amount)
        );


    }

    private void validatCancelBalance(Transaction transaction, Account account, Long amount) {
       if(!Objects.equals(transaction.getAccount().getId(), account.getId())) {
           throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH);
       }
       if(!Objects.equals(transaction.getAmount(), amount)) {
           throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
       }
       if(transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
           throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
       }
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(CANCEL,F, account, amount);
    }

    public TransactionDto queryTransactionId(String transactionId) {
        return TransactionDto.fromEntity( transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }

//    private Transaction saveAndGetTransaction(Long amount, Account account) {
//        return saveAndGetTransaction(F, account, amount);
//    }
}
