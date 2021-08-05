package com.hey.payment.exception_handler.exception;

public class DatabaseHasErr extends Exception{
    public DatabaseHasErr(){
        super("Database has error!");
    }
}
