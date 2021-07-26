package com.hey.lucky.exception_handler.exception;

public class LuckyMoneyInvalidException extends RuntimeException{
    public LuckyMoneyInvalidException(){
        super("This lucky money is not exist");
    }
}
