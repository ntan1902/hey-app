package com.hey.payment.exception_handler.exception;

public class SoftTokenAuthorizeException extends RuntimeException{
    public SoftTokenAuthorizeException(String message){
        super(message);
    }
}
