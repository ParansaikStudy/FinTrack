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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.track.fin.type.AccountStatus.CLOSED;
import static com.track.fin.type.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserService userService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance, AccountType accountType) {
        User user = userService.get(userId);

        validateCreateAccount(user);
        validateInitialBalance(initialBalance, accountType);

        String newAccountNumber = generateUniqueAccountNumber();
        Account account = null;
                /*accountRepository.save(Account.builder()
                .user(user)
                .accountStatus(ACTIVE)
                .accountNumber(newAccountNumber)
                .balance(initialBalance)
                .accountType(accountType)
                .build());*/

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
    public AccountDto deleteAccount(Long userId, String accountNumber, String withdrawAccountNumber) {
        User user = userService.get(userId);
        Account closingAccount = getAccountByNumber(accountNumber);
        Account withdrawAccount = getAccountByNumber(withdrawAccountNumber);

        validateDeleteAccount(user, closingAccount, withdrawAccount);

        // 여기서 검증할 필요가 있나?
        if (closingAccount.getBalance() > 0) {
            transactionService.transfer(userId, accountNumber, withdrawAccountNumber, closingAccount.getBalance());
        }

        closingAccount.close();


        return AccountRecord.from(accountRepository.save(closingAccount));
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

    private void validateDeleteAccount(User user, Account closingAccount, Account withdrawAccount) {
        if (!Objects.equals(user.getId(), closingAccount.getUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (closingAccount.getAccountStatus() == CLOSED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (!Objects.equals(user.getId(), withdrawAccount.getUser().getId())) {
            throw new AccountException(WITHDRAW_ACCOUNT_UNMATCH);
        }
        if (withdrawAccount.getAccountStatus() != ACTIVE) {
            throw new AccountException(WITHDRAW_ACCOUNT_INACTIVE);
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

    @Transactional
    public AccountDto restoreAccount(Long userId, String accountNumber) {
        User user = userService.get(userId);
        Account account = getAccountByNumber(accountNumber);

        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }

        if (account.getAccountStatus() != CLOSED) {
            throw new AccountException(INVALID_REQUEST);
        }

        if (account.getUnregisteredAt() == null) {
            throw new AccountException(INVALID_REQUEST);
        }

        if (account.getUnregisteredAt().isBefore(LocalDateTime.now().minusMonths(3))) {
            accountRepository.delete(account);
            throw new AccountException(ACCOUNT_RESTORE_EXPIRED);
        }

        account.restore();
        return AccountRecord.from(accountRepository.save(account));
    }

}
