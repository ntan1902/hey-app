package com.hey.lucky.exception_handler.exception;

public class CannotTransferMoneyException extends RuntimeException{
    public CannotTransferMoneyException(String message){
        super(message);
    }
}
