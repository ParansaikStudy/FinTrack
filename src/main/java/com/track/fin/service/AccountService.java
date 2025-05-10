package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.dto.AccountDto;
import com.track.fin.exception.AccountException;
import com.track.fin.record.AccountRecord;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.UserRepository;
import com.track.fin.type.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.track.fin.type.AccountStatus.ACTIVE;
import static com.track.fin.type.AccountStatus.CLOSED;
import static com.track.fin.type.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserService userService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance, AccountType accountType) {
        User user = userService.get(userId);

        validateCreateAccount(user);
        validateInitialBalance(initialBalance, accountType);

        String newAccountNumber = generateUniqueAccountNumber();
        Account account = accountRepository.save(Account.builder()
                .user(user)
                .accountStatus(ACTIVE)
                .accountNumber(newAccountNumber)
                .balance(initialBalance)
                .accountType(accountType)
                .build());

        return AccountRecord.from(account);
    }

    @Transactional
    public Account getAccount(Long accountId) {
        if (accountId < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(accountId).get();
    }

    @Transactional
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
    }

    public BigDecimal getAccountCollateralRate(Long userId, Long accountId) {
        User user = userService.get(userId);
        Account account = this.getAccount(accountId);
        return user.getGrade().getGradeType().getCollateralRate().multiply(BigDecimal.valueOf(account.getBalance()));
    }

    @Transactional
    public List<Account> getAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        User user = userService.get(userId);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validateDeleteAccount(user, account);
        account.afterLoan();

        return AccountRecord.from(accountRepository.save(account));
    }

    @Transactional

    public List<AccountDto> getAccountsByuserId(Long userId) {
        User user = userService.get(userId);

        List<Account> accounts = accountRepository.findByUser(user);

        return accounts.stream()
                .map(AccountRecord::from)
                .collect(Collectors.toList());
    }

    private void validateCreateAccount(User user) {
        if (accountRepository.countByUser(user) == 10) {
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
    }


    private void validateDeleteAccount(User user, Account account) {
        if (!Objects.equals(user.getId(), account.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() == CLOSED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    private void validateInitialBalance(Long initialBalance, AccountType accountType) {
        if (accountType.getBalance().equals(initialBalance)) {
            throw new AccountException(INSUFFICIENT_INITIAL_BALANCE);
        }
    }

    private Double calculatorRate(AccountType accountType) {
        return accountType.getBalance() * accountType.getRate();
    }

    private String generateUniqueAccountNumber() {
        return String.valueOf(1000000000L + Math.random() * 9000000000L);
    }

}
