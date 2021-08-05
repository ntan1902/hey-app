package com.hey.payment.exception_handler.exception;

public class WrongSourceException extends Exception{
    public WrongSourceException(){
        super("Source of money is wrong!");
    }
}
