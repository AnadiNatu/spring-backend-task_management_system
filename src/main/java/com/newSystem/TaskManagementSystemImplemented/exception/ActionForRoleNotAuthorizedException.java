package com.newSystem.TaskManagementSystemImplemented.exception;

public class ActionForRoleNotAuthorizedException extends RuntimeException {

    private static final long serialVersionUID = 3;

    public ActionForRoleNotAuthorizedException(String message){
        super(message);
    }
}
