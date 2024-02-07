package com.example.webapp.controlleradvice;

public class InvalidUserUpdaRequestException extends Exception{
    public InvalidUserUpdaRequestException(String message) {
        super(message);
    }
}
