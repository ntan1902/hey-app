package com.hey.lucky.exception_handler.exception;

public class CannotGetUserInfo extends RuntimeException{
    public CannotGetUserInfo(){
        super("Can not get info!");
    }
}
