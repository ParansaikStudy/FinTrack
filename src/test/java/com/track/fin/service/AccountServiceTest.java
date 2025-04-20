package com.track.fin.service;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import com.track.fin.dto.AccountDto;
import com.track.fin.exception.AccountException;
import com.track.fin.repository.AccountUserRepository;
import com.track.fin.type.AccountStatus;
import com.track.fin.repository.AccountRepository;
import com.track.fin.type.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess() {

       User user = User.builder()
                .id(12L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012").build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .user(user)
                        .accountNumber("1000000015").build());
        ArgumentCaptor<Account>  captor = ArgumentCaptor.forClass(Account.class);
        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        System.out.println("Captured Account Number: " + captor.getValue().getAccountNumber());

    //    assertEquals("1000000013", captor.getValue().getAccountNumber());


    }

    @Test
    void createFirstAccount() {

        User user = User.builder()
                .id(15L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .user(user)
                        .accountNumber("1000000013").build());



        ArgumentCaptor<Account>  captor = ArgumentCaptor.forClass(Account.class);
        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());


    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌생성 실패")
    void createAccount_UserNotFound() {
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());



        //when
        AccountException exception =  assertThrows(AccountException.class,
               () -> accountService.createAccount(1L, 1000L));


      Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());


    }
    @Test
    @DisplayName("유저당 최대 계좌는 10개")
    void createAccount_maxAccountIs10() {
        User user = User.builder()
                .id(15L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        Assertions.assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());

    }

    @Test
    void deleteAccountSuccess() {

        User user = User.builder()
                .id(12L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .user(user)
                        .balance(0L)
                        .accountNumber("1000000012").build()));
        ArgumentCaptor<Account>  captor = ArgumentCaptor.forClass(Account.class);
        //when
        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012",captor.getValue().getAccountNumber());
        Assertions.assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());




    }
    @Test
    @DisplayName("계좌 소유주 다름")
    void deteteAccountFaild_userUnMatch() {

        User povi = User.builder()
                .id(12L)
                .name("Povi").build();
        User harry = User.builder()
                .id(13L)
                .name("Harry").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(povi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .user(harry)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        AccountException exception =  assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));


        Assertions.assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());



    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deteteAccountFaild_balanceNotEmpty() {

        User povi = User.builder()
                .id(12L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(povi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .user(povi)
                        .balance(100L)
                        .accountNumber("1000000012").build()));

        AccountException exception =  assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));


        Assertions.assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());



    }

    @Test
    @DisplayName("해지 계좌는 해지할 수 없다.")
    void deteteAccountFaild_alreadyungrestered() {

        User povi = User.builder()
                .id(12L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(povi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .user(povi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        AccountException exception =  assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));


        Assertions.assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());



    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌해지 실패")
    void deleteAccount_UserNotFound() {

        User user = User.builder()
                .id(15L)
                .name("Povi").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        //when
        AccountException exception =  assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));


        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());


    }
    @Test
    void successGetAccountsByuserId() {
        User povi = User.builder()
                .id(12L)
                .name("Povi").build();
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .user(povi)
                        .accountNumber("1111111111")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .user(povi)
                        .accountNumber("2222222222")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .user(povi)
                        .accountNumber("3333333333")
                        .balance(3000L)
                        .build()
        );
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(povi));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        //when
        List<AccountDto> accountDtos =  accountService.getAccountsByuserId(1L);
        //then
        assertEquals(3, accountDtos.size());
        assertEquals("1111111111" , accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals("2222222222" , accountDtos.get(1).getAccountNumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals("3333333333" , accountDtos.get(2).getAccountNumber());
        assertEquals(3000, accountDtos.get(2).getBalance());

    }
    @Test
    void failedToGetAccounts() {
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountsByuserId(1L));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    }




}