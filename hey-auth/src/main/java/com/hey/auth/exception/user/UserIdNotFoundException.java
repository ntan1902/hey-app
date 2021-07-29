package com.hey.auth.exception.user;

public class UserIdNotFoundException extends RuntimeException{
    public UserIdNotFoundException(String message) {
        super(message);
    }
}
