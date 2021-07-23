package com.hey.payment.exception_handler.exception;

public class ApiErrException extends RuntimeException {
    public ApiErrException(String message) {
        super(message);
    }
}
