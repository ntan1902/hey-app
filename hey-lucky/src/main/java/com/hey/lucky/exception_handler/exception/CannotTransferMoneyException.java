package com.hey.lucky.exception_handler.exception;

public class CannotTransferMoneyException extends RuntimeException{
    public CannotTransferMoneyException(){
        super("Can not transfer money!");
    }
}
