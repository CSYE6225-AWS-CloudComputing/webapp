package com.example.webapp.controlleradvice;

public class UnAuthorizedException extends Exception{

    public UnAuthorizedException(String message) {
        super(message);
    }
}
