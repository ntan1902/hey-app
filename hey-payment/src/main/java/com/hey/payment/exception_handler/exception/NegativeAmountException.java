package com.hey.payment.exception_handler.exception;

public class NegativeAmountException extends Exception{
    public NegativeAmountException() {
        super("Negative amount");
    }
}
