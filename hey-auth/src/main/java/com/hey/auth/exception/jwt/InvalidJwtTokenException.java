package com.hey.auth.exception.jwt;

public class InvalidJwtTokenException extends Exception{
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
