package com.hey.auth.exception.user;

public class PinNotMatchedException extends Exception{
    public PinNotMatchedException(String message) {
        super(message);
    }
}
