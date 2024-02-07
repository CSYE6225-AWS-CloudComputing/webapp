package com.example.webapp.controlleradvice;

public class UserDoesNotExistException extends Exception{

    public UserDoesNotExistException(String message) {
        super(message);
    }
}
