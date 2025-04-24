package com.track.fin.exception;

import com.track.fin.type.ErrorCode;

public class FeeException extends RuntimeException {

    private ErrorCode errorCode;

    private String errorMessage;


    public FeeException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
