package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.exception.AccountException;
import com.track.fin.record.AccountRecord;
import com.track.fin.record.DeleteAccountRecord;
import com.track.fin.type.TransactionMethodType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.track.fin.design.singleton.AccountValidates.validateDeleteAccount;
import static com.track.fin.design.singleton.AccountValidates.validateRestoreAccount;
import static com.track.fin.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountFacadeService {

    private final UserService userService;
    private final AccountService accountService;
    private final LoanService loanService;
    private final TransactionService transactionService;


    @Transactional
    public AccountRecord deleteAccount(DeleteAccountRecord deleteAccountRecord) {
        User user = userService.get(deleteAccountRecord.userId());
        Account closingAccount = accountService.getAccountByNumber(deleteAccountRecord.accountNumber());
        Account withdrawAccount = accountService.getAccountByNumber(deleteAccountRecord.withdrawAccountNumber());

        validateDeleteAccount(user, closingAccount, withdrawAccount);

        transferRemainingBalanceIfExists(deleteAccountRecord, closingAccount);

        closingAccount.close();
        return AccountRecord.from(accountService.createAccount(closingAccount));
    }

    @Transactional
    public AccountRecord restoreAccount(Long userId, String accountNumber) {
        User user = userService.get(userId);
        Account account = accountService.getAccountByNumber(accountNumber);

        validateRestoreAccount(user, account);
        account.restore();

        return AccountRecord.from(accountService.createAccount(account));
    }

    @Transactional
    public void deleteIfExpired(Account account) {
        if (account.getUnregisteredAt() != null &&
                account.getUnregisteredAt().isBefore(LocalDateTime.now().minusMonths(3))) {
            accountService.deleteAccount(account);
            throw new AccountException(ACCOUNT_RESTORE_EXPIRED);
        }
    }

    private void transferRemainingBalanceIfExists(DeleteAccountRecord record, Account closingAccount) {
        if (closingAccount.getBalance() > 0) {
            transactionService.transfer(
                    record.userId(),
                    record.accountNumber(),
                    record.withdrawAccountNumber(),
                    closingAccount.getBalance(),
                    TransactionMethodType.AUTO
            );
        }
    }
   // TODO : 대출 로직 구현 후 사용 예정
   private void validateUnpaidLoan(Account account) {
       if (loanService.existsUnpaidLoanByAccount(account)) {
           throw new AccountException(LOAN_EXISTS);
       }
   }

    private void validateAutoTransfer(Account account) {
        if (transactionService.existsByAccountAndTransactionMethodType(account, TransactionMethodType.AUTO)) {
            throw new AccountException(AUTO_TRANSFER_EXISTS);
        }
    }

}
