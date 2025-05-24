package com.track.fin.design.singleton;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.exception.AccountException;
import com.track.fin.type.AccountType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.track.fin.type.AccountStatus.ACTIVE;
import static com.track.fin.type.AccountStatus.CLOSED;
import static com.track.fin.type.ErrorCode.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountValidates {

    public static void validateDeleteAccount(User user, Account closingAccount, Account withdrawAccount) {
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
        if (closingAccount.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    public static void validateInitialBalance(Long initialBalance, AccountType accountType) {
        if (accountType.getMinimumBalance().equals(initialBalance)) {
            throw new AccountException(INSUFFICIENT_INITIAL_BALANCE);
        }
    }

    public static void validateRestoreAccount(User user, Account account) {
        if (!Objects.equals(account.getUser().getId(), user.getId())) {
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() != CLOSED) {
            throw new AccountException(INVALID_REQUEST);
        }
        if (account.getUnregisteredAt() == null) {
            throw new AccountException(INVALID_REQUEST);
        }
    }

}
