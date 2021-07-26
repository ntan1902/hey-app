package com.hey.authentication.exception.user;

public class EmptyPinException extends RuntimeException{
    public EmptyPinException(String message) {
        super(message);
    }
}
