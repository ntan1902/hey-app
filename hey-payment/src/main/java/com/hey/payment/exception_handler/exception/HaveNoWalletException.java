package com.hey.payment.exception_handler.exception;

public class HaveNoWalletException extends Exception{
    public HaveNoWalletException(String message){
        super(message);
    }
}
