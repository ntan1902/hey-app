package com.hey.auth.exception.user;

public class PasswordNotMatchedException extends Exception{
    public PasswordNotMatchedException(String message) {
        super(message);
    }
}
