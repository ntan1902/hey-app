package com.hey.payment.exception_handler.exception;

public class WalletNotExistException extends Exception{
    public WalletNotExistException(long walletId){
        super("Wallet "+walletId+" is not exist");
    }
}
