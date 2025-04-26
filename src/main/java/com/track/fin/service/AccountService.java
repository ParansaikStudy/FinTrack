package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.dto.AccountDto;
import com.track.fin.exception.AccountException;
import com.track.fin.record.AccountRecord;
import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.UserRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.type.AccountType;
import com.track.fin.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.track.fin.type.ErrorCode.USER_NOT_FOUND;

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
                .accountStatus(AccountStatus.ACTIVE)
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
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
    }


    // TODO: 특정 계좌 담보 대출 가능한 금액 출력
    public BigDecimal getAccountCollateralRate(Long userId, Long accountId) {
        User user = userService.get(userId);
        Account account = this.getAccount(accountId);
        // 회원 등급에 따른 %
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

        List<Account> accounts = accountRepository
                .findByUser(user);

        return accounts.stream()
                .map(AccountRecord::from)
                .collect(Collectors.toList());
    }

    private void validateCreateAccount(User user) {
        if (accountRepository.countByUser(user) == 10) {
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


    // 정수 숫자를 사용 하면 X
    // 1. 재사용성 x
    // 2. 계산 할 때 오타 -> 컴파일 시점에서 오류가 안생겨서 에러 잡기 힘듦
    // 3. 코드 리뷰할 때 이
    private void validateInitialBalance(Long initialBalance, AccountType accountType) {
        if (accountType.getBalance().equals(initialBalance)) {
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
        return accountType.getBalance() * accountType.getRate();
    }

    private String generateUniqueAccountNumber() {
        return String.valueOf(1000000000L + Math.random() * 9000000000L);
    }

}
