package com.track.fin.service;

import com.track.fin.domain.User;
import com.track.fin.exception.AccountException;
import com.track.fin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.track.fin.type.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User get(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }

}
