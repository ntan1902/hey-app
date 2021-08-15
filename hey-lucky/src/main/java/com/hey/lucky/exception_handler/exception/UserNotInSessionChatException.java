package com.hey.lucky.exception_handler.exception;

public class UserNotInSessionChatException extends Exception {
    public UserNotInSessionChatException(String message){
        super(message);
    }
}
