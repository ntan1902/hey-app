package com.hey.auth.exception.user;

public class PinNotMatchedException extends RuntimeException{
    public PinNotMatchedException(String message) {
        super(message);
    }
}
