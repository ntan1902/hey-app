package com.hey.payment.exception_handler.exception;

public class BankInvalidException extends Exception{
    public BankInvalidException(){
        super("Bank is invalid!");
    }
}
