package com.hey.auth.exception.system;

public class SystemKeyInvalidException extends RuntimeException{
    public SystemKeyInvalidException(String message) {
        super(message);
    }
}