package com.example.webapp.controlleradvice;

import java.io.IOException;

public class PubSubException extends Exception{

    public PubSubException(String message) {
        super(message);
    }

    public PubSubException(String message, IOException e) {
    }
}
