package com.hey.auth.exception.user;

public class UsernameEmailExistedException extends RuntimeException{
    public UsernameEmailExistedException(String message) {
        super(message);
    }
}
