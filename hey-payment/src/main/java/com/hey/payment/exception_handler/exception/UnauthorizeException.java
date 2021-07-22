package com.hey.payment.exception_handler.exception;

public class UnauthorizeException extends RuntimeException{
    public UnauthorizeException(String message){
        super(message);
    }
}
