package com.example.Leave_Management_system.Exception;

// Thrown when a user tries to do something they're not allowed to
// Ex: an EMPLOYEE trying to approve a leave
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message){
        super(message);
    }
}
