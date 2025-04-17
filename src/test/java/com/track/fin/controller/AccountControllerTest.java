//package com.track.fin.controller;
//
//import com.track.fin.domain.Account;
//import com.track.fin.dto.AccountDto;
//import com.track.fin.dto.CreateAccount;
//import com.track.fin.dto.DeleteAccount;
//import com.track.fin.type.AccountStatus;
//import com.track.fin.service.AccountService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AccountController.class)
//class AccountControllerTest {
//    @MockBean
//    private AccountService accountService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void successCreateAccount() throws Exception {
//        given(accountService.createAccount(anyLong(), anyLong()))
//                .willReturn(AccountDto.builder()
//                        .userId(1L)
//                        .accountNumber("1234567890")
//                        .registeredAt(LocalDateTime.now())
//                        .build());
//
//        mockMvc.perform(post("/account")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(
//                        new CreateAccount.Request(3333L, 1111L)
//                )))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(1))
//                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
//                .andDo(print());
//
//    }
//
//    @Test
//    void successDeleteAccount() throws Exception {
//        given(accountService.deleteAccount(anyLong(), anyString()))
//                .willReturn(AccountDto.builder()
//                        .userId(1L)
//                        .accountNumber("1234567890")
//                        .registeredAt(LocalDateTime.now())
//                        .build());
//
//        mockMvc.perform(delete("/account")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(
//                                new DeleteAccount.Request(3333L, "0987654321")
//                        )))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(1))
//                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
//                .andDo(print());
//
//    }
//    @Test
//    void successGetAccountsByuserId() throws Exception {
//        //given
//        List<AccountDto> accountDtos =
//                Arrays.asList(
//                        AccountDto.builder()
//                                .accountNumber("1234567890")
//                                .balance(1000L).build(),
//                        AccountDto.builder()
//                                .accountNumber("1111111111")
//                                .balance(2000L).build(),
//                        AccountDto.builder()
//                                .accountNumber("2222222222")
//                                .balance(3000L).build()
//                );
//        given(accountService.getAccountsByuserId(anyLong()))
//                .willReturn(accountDtos);
//
//
////      when
////        then
//        mockMvc.perform(get("/account?user_id=1"))
//                .andDo(print())
//                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
//                .andExpect(jsonPath("$[0].balance").value(1000))
//                .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
//                .andExpect(jsonPath("$[1].balance").value(2000))
//                .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
//                .andExpect(jsonPath("$[2].balance").value(3000));
//
//
//    }
//
//    @Test
//    void successGetAccount() throws Exception {
//        //given
//        given(accountService.getAccount(anyLong()))
//                .willReturn(Account.builder()
//                        .accountNumber("3456")
//                        .accountStatus(AccountStatus.IN_USE)
//                        .build());
//
//        //when
//        //then
//        mockMvc.perform(get("/account/876"))
//                .andDo(print())
//                .andExpect(jsonPath("$.accountNumber").value("3456"))
//                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
//                .andExpect(status().isOk());
//    }
//}