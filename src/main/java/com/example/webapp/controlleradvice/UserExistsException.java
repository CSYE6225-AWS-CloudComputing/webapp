package com.example.webapp.controlleradvice;

public class UserExistsException extends Exception{

    public UserExistsException(String message) {
        super(message);
    }
}
