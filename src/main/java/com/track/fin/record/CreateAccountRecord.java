package com.track.fin.record;

public record CreateAccountRecord(

        Long userId,

        String accountNumber

) {

    public static CreateAccountRecord from(AccountRecord accountRecord) {
        return new CreateAccountRecord(
                accountRecord.userId(),
                accountRecord.accountNumber()
        );
    }

}
