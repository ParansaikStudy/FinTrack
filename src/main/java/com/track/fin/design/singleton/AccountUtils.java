package com.track.fin.design.singleton;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountUtils {

    private static final long ACCOUNT_NUMBER_BASE = 1_000_000_000L;
    private static final long ACCOUNT_NUMBER_RANGE = 9_000_000_000L;

    public static String generateUniqueAccountNumber() {
        long randomNumber = (long) (Math.random() * ACCOUNT_NUMBER_RANGE);
        return String.valueOf(ACCOUNT_NUMBER_BASE + randomNumber);
    }

}
