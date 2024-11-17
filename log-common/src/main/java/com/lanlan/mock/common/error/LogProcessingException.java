package com.lanlan.mock.common.error;

public class LogProcessingException extends RuntimeException {
    private final ErrorCode errorCode;

    public LogProcessingException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public LogProcessingException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

