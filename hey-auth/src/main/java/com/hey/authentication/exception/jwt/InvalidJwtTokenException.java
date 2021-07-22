package com.hey.authentication.exception.jwt;

public class InvalidJwtTokenException extends RuntimeException{
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
