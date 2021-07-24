package com.hey.payment.exception_handler.exception;

public class MaxAmountException extends RuntimeException{
    public MaxAmountException(Long maxAmount){
        super("Transaction limit is " + maxAmount);
    }
}
