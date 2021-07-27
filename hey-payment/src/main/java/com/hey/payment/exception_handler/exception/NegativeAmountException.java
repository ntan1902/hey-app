package com.hey.payment.exception_handler.exception;

public class NegativeAmountException extends RuntimeException{
    public NegativeAmountException() {
        super("Negative amount");
    }
}
