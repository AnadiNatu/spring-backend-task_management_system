package com.newSystem.TaskManagementSystemImplemented.exception;

public class InvalidActionException extends RuntimeException{
    private static final long serialVersionUID = 1;
    public InvalidActionException(String message){
        super(message);
    }
}
