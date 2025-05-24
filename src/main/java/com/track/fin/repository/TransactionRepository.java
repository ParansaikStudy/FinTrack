package com.track.fin.repository;

import com.track.fin.domain.Account;
import com.track.fin.domain.Transaction;
import com.track.fin.type.TransactionMethodType;
import com.track.fin.type.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findById(String transactionId);

    boolean existsByAccountAndTransactionMethodType(Account account, TransactionMethodType methodType);

    List<Transaction> findByAccountAndTransactionDateBetweenAndTransactionTypeIn(
            Account account,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<TransactionType> types
    );

    List<Transaction> findByAccountAndTransactionDateBetween(
            Account account,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction> findByAccountAndTransactionTypeAndTransactionDateBetween(
            Account account,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

}
