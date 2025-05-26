package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.exception.AccountException;
import com.track.fin.record.AccountRecord;
import com.track.fin.record.CreateAccount;
import com.track.fin.record.DeleteAccountRecord;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.UserRepository;
import com.track.fin.type.TransactionMethodType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.track.fin.design.singleton.AccountUtils.generateUniqueAccountNumber;
import static com.track.fin.design.singleton.AccountValidates.*;
import static com.track.fin.type.AccountStatus.ACTIVE;
import static com.track.fin.type.AccountStatus.CLOSED;
import static com.track.fin.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final UserService userService;
    private final LoanService loanService;
    private final UserRepository userRepository;

    @Transactional
    public Account createAccount(CreateAccount createAccount) {
        User user = userService.get(createAccount.userId());

        validateCreateAccount(user);
        validateInitialBalance(createAccount.initialBalance(), createAccount.accountType());

        return accountRepository.save(Account.from(user, createAccount, generateUniqueAccountNumber()));
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

    private void validateCreateAccount(User user) {
        if (accountRepository.countByUser(user) == 10) {
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
        if (hasClosedAccountWithinLastMonth(user)) {
            throw new AccountException(CANNOT_CREATE_ACCOUNT_DUE_TO_RECENT_CLOSURE);
        }
    }

    private boolean hasClosedAccountWithinLastMonth(User user) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        return accountRepository.findByUser(user).stream()
                .filter(account -> account.getAccountStatus() == CLOSED)
                .anyMatch(account -> {
                    LocalDateTime closedAt = account.getUnregisteredAt();
                    return closedAt != null && closedAt.isAfter(oneMonthAgo);
                });
    }

    @Transactional(readOnly = true)
    public List<Account> getActiveAccounts(Long userId) {
        User user = userService.get(userId);
        return accountRepository.findByUserAndAccountStatus(user, ACTIVE);
    }

    @Transactional
    public AccountRecord restoreAccount(Long userId, String accountNumber) {
        User user = userService.get(userId);
        Account account = getAccountByNumber(accountNumber);

        validateRestoreAccount(user, account);

        account.restore();
        return AccountRecord.from(accountRepository.save(account));
    }

    public boolean validateAutoTransferNotRegistered(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);

        if (Boolean.TRUE.equals(account.getAutoTransfer())) {
            throw new AccountException(AUTO_TRANSFER_ACTIVE);
        }

        return true;
    }

    @Transactional(readOnly = true)
    public boolean isAutoTransferRegistered(String accountNumber) {
        return this.findByAccountNumber(accountNumber).getAutoTransfer();
    }

    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
    }

}
