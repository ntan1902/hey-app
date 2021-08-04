package com.hey.payment.exception_handler.exception;

public class BalanceNotEnoughException extends Exception{
    public BalanceNotEnoughException(){
        super("Your balance not enough");
    }
}
