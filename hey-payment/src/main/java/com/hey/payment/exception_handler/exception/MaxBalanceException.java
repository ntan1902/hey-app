package com.hey.payment.exception_handler.exception;

public class MaxBalanceException extends RuntimeException{
    public MaxBalanceException(){
        super("Destination can't receive more money!");
    }
}
