package com.hey.auth.exception.user;

public class EmptyPinException extends RuntimeException{
    public EmptyPinException(String message) {
        super(message);
    }
}
