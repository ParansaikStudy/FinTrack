package com.track.fin.repository;

import com.track.fin.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountUserRepository extends JpaRepository <AccountUser, Long> {


}
