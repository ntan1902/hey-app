package com.hey.payment.exception_handler.exception;

public class WrongTargetException extends Exception{
    public WrongTargetException(){
        super("Destination is wrong");
    }
}
