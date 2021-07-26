package com.hey.lucky.exception_handler.exception;

public class HadReceivedException extends RuntimeException{
    public HadReceivedException(){
        super("You had received this lucky money");
    }
}
