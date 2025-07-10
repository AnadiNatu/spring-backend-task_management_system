package com.newSystem.TaskManagementSystemImplemented.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<ErrorObject> handleInvalidActionException(InvalidActionException ex){

        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimeStamp(new Date());

        return new ResponseEntity<>(errorObject , HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex){

        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimeStamp(new Date());

        return new ResponseEntity<>(errorObject , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActionForRoleNotAuthorizedException.class)
    public ResponseEntity<ErrorObject> handleActionForRoleNotAuthorizedException(ActionForRoleNotAuthorizedException ex){

        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimeStamp(new Date());

        return new ResponseEntity<>(errorObject , HttpStatus.FORBIDDEN);
    }

}
