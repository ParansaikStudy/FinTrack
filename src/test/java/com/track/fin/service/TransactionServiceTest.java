package com.track.fin.service;

import com.track.fin.repository.AccountRepository;
import com.track.fin.repository.AccountUserRepository;
import com.track.fin.repository.TransactionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;

//    @Test
//    void successUseBalance() {
//        AccountUser user = AccountUser.builder()
//                .id(12L)
//                .name("Povi").build();
//        Account account = Account.builder()
//                        .accountUser(user)
//                        .accountStatus(IN_USE)
//                        .balance(10000L)
//                        .accountNumber("1000000012").build();
//
//        given(accountUserRepository.findById(anyLong()))
//                .willReturn(Optional.of(user));
//        given(accountRepository.findByAccountNumber(anyString()))
//                .willReturn(Optional.of(account));
//        given(transactionRepository.save(any()))
//                .willReturn(Transaction.builder()
//                        .account(account)
//                        .transactionType(USE)
//                        .transactionResultType(S)
//                        .transactionId("transactionId")
//                        .transactedAt(LocalDateTime.now())
//                        .amount(1000L)
//                        .balanceSnapshot(9000L)
//                        .build());
//
//        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
//
//        TransactionDto transactionDto = transactionService.useBalance(1L,"1000000000", "200L");
//
//
//
//        Mockito.verify(transactionRepository, times(1)).save(captor.capture());
//        assertEquals(S, transactionDto.getTransactionResultType());
//        assertEquals(USE, transactionDto.getTransactionType());
//        assertEquals(9000L, transactionDto.getBalanceSnapshot());
//        assertEquals(1000L, transactionDto.getAmount());
//
//
//
//    }


}