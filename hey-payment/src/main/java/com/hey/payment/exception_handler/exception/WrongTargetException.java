package com.hey.payment.exception_handler.exception;

public class WrongTargetException extends RuntimeException{
    public WrongTargetException(){
        super("Destination is wrong");
    }
}
