package com.hey.payment.exception_handler.exception;

public class BalanceNotEnoughException extends RuntimeException{
    public BalanceNotEnoughException(){
        super("Your balance not enough");
    }
}
