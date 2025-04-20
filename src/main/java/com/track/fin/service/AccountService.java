package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.dto.AccountDto;
import com.track.fin.record.AccountRecord;
import com.track.fin.exception.AccountException;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.AccountUserRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import com.track.fin.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.track.fin.type.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final int SAVINGS_DEFAULT_AMOUNT = 1000;

    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance, AccountType accountType) {
        User user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validateCreateAccount(user);
        validateInitialBalance(initialBalance, accountType);

        String newAccountNumber = generateUniqueAccountNumber();
        Account account = accountRepository.save(Account.builder()
                .user(user)
                .accountStatus(AccountStatus.ACTIVE)
                .accountNumber(newAccountNumber)
                .balance(initialBalance)
                .accountType(accountType)
                .registeredAt(LocalDateTime.now())
                .build());

        return AccountRecord.from(account);
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        User user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validateDeleteAccount(user, account);

        account.setAccountStatus(AccountStatus.CLOSED);
        account.setUnregisteredAt(LocalDateTime.now());

        return AccountRecord.from(accountRepository.save(account));
    }

    @Transactional
    public List<AccountDto> getAccountsByuserId(Long userId) {
        User user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        List<Account> accounts = accountRepository
                .findByAccountUser(user);

        return accounts.stream()
                .map(AccountRecord::from)
                .collect(Collectors.toList());
    }

    private void validateCreateAccount(User user) {
        if (accountRepository.countByAccountUser(user) == 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    private void validateDeleteAccount(User user, Account account) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }

    private void validateInitialBalance(Long initialBalance, AccountType accountType) {
        if (initialBalance < accountType.getDefaultBalance()) {
            throw new AccountException(ErrorCode.INSUFFICIENT_INITIAL_BALANCE);
        }
    }

    private Double calculatorRate(AccountType accountType) {
        return accountType.getDefaultBalance() * accountType.getDefaultRate();
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;

        do {
            accountNumber = String.valueOf(1000000000L + Math.random() * 9000000000L);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
