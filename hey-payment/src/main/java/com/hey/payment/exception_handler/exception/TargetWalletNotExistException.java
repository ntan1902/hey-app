package com.hey.payment.exception_handler.exception;

public class TargetWalletNotExistException extends RuntimeException{
    public TargetWalletNotExistException(){
        super("Destination is wrong");
    }
}
