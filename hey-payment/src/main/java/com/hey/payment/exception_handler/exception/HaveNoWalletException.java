package com.hey.payment.exception_handler.exception;

public class HaveNoWalletException extends RuntimeException{
    public HaveNoWalletException(){
        super("Have no wallet yet");
    }
}
