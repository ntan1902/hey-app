package com.hey.payment.exception_handler.exception;

public class BankInvalidException extends RuntimeException{
    public BankInvalidException(){
        super("Bank is invalid!");
    }
}
