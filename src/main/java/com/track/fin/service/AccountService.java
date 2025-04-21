package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.AccountUser;
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

import static com.track.fin.type.ErrorCode.ACCOUNT_NOT_FOUND;
import static com.track.fin.type.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final int SAVINGS_DEFAULT_AMOUNT = 1000;

    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance, AccountType accountType) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validateCreateAccount(accountUser);
        validateInitialBalance(initialBalance, accountType);

        String newAccountNumber = generateUniqueAccountNumber();
        Account account = accountRepository.save(Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
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
    public List<AccountDto> getAccountsByuserId(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        List<Account> accounts = accountRepository
                .findByAccountUser(accountUser);

        return accounts.stream()
                .map(AccountRecord::from)
                .collect(Collectors.toList());
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) == 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnregisteredAt(LocalDateTime.now());

        return AccountRecord.from(accountRepository.save(account));
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }
        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }


    // 정수 숫자를 사용 하면 X
    // 1. 재사용성 x
    // 2. 계산 할 때 오타 -> 컴파일 시점에서 오류가 안생겨서 에러 잡기 힘듦
    // 3. 코드 리뷰할 때 이
    private void validateInitialBalance(Long initialBalance, AccountType accountType) {
        if (initialBalance < accountType.getDefaultBalance()) {
            throw new AccountException(ErrorCode.INSUFFICIENT_INITIAL_BALANCE);
        }
    }

    // 개발 잘하시는 분들은 enum을 기가막히게 쓰십니다
    // 그래서 enum 공부 많이 하세요
    // review 용
    private Double calculatorRate(AccountType accountType) {
//        double rate = 0.0D;
//        if (accountType.equals(AccountType.SAVINGS)) {
//            rate = 10000000 * 0.1;
//        } else if (accountType.equals(AccountType.DEPOSIT.DEPOSIT)) {
//            rate = 10000 * 0.2;
//        } else {
//            rate = 0;
//        }
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
