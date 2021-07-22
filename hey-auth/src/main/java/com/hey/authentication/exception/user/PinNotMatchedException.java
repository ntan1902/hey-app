package com.hey.authentication.exception.user;

public class PinNotMatchedException extends RuntimeException{
    public PinNotMatchedException(String message) {
        super(message);
    }
}
