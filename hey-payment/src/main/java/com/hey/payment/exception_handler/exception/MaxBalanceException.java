package com.hey.payment.exception_handler.exception;

public class MaxBalanceException extends Exception{
    public MaxBalanceException(){
        super("Target can't receive more money!");
    }
}
