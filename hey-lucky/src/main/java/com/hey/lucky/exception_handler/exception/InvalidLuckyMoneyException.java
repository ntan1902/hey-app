package com.hey.lucky.exception_handler.exception;

public class InvalidLuckyMoneyException extends Exception{
    public InvalidLuckyMoneyException(){
        super("This lucky money is not exist");
    }
}
