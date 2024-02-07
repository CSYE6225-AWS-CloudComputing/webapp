package com.example.webapp.controlleradvice;

public class InvalidCreateRequest extends Exception{

    public InvalidCreateRequest(String message) {
        super(message);
    }
}
