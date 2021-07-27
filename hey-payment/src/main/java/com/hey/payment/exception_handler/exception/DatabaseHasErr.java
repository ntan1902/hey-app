package com.hey.payment.exception_handler.exception;

public class DatabaseHasErr extends RuntimeException{
    public DatabaseHasErr(){
        super("Database has error!");
    }
}
