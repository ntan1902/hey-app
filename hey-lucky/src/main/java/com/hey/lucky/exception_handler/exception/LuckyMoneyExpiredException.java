package com.hey.lucky.exception_handler.exception;

public class LuckyMoneyExpiredException extends Exception{
    public LuckyMoneyExpiredException(){
        super("Lucky money is expired");
    }
}
