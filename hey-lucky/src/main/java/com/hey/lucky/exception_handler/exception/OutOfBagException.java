package com.hey.lucky.exception_handler.exception;

public class OutOfBagException extends RuntimeException{
    public OutOfBagException(){
        super("Out of bag!");
    }
}
