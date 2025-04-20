package com.track.fin.repository;

import com.track.fin.domain.Account;
import com.track.fin.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();

    Integer countByAccountUser(User user);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByAccountUser(User user);

    boolean existsByAccountNumber(String accountNumber);
}
