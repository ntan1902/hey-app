package com.hey.auth.exception.user;

public class UsernameEmailExistedException extends Exception{
    public UsernameEmailExistedException(String message) {
        super(message);
    }
}
