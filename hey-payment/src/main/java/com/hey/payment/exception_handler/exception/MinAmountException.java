package com.hey.payment.exception_handler.exception;

import static com.hey.payment.constant.MoneyConstant.MIN_AMOUNT;

public class MinAmountException extends Exception{
    public MinAmountException() {
        super("Min amount is " + MIN_AMOUNT);
    }
}
