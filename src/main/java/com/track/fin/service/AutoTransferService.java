package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.exception.AccountException;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.TransactionRepository;
import com.track.fin.type.TransactionMethodType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.track.fin.type.ErrorCode.AUTO_TRANSFER_ACTIVE;

@Service
@RequiredArgsConstructor
public class AutoTransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public void validateAutoTransferNotRegistered(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(AUTO_TRANSFER_ACTIVE));

        if (Boolean.TRUE.equals(account.getAutoTransfer())) {
            throw new AccountException(AUTO_TRANSFER_ACTIVE);
        }
    }

    @Transactional(readOnly = true)
    public boolean isAutoTransferRegistered(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getAutoTransfer)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean existsByAccount(Account account) {
        return transactionRepository.existsByAccountAndTransactionMethodType(account, TransactionMethodType.AUTO);
    }

}
