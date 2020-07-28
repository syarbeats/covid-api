package com.mitrais.cdc.covid19backend.controller;

public class ControllerException extends RuntimeException{

    public ControllerException(String message) {
        super(message);
    }
}
