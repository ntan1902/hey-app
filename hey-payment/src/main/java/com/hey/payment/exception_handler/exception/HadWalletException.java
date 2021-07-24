package com.hey.payment.exception_handler.exception;

public class HadWalletException extends RuntimeException{
    public HadWalletException(){
        super("You had a wallet");
    }
}
