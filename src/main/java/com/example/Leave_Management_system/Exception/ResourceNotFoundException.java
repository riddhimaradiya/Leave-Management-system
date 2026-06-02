package com.example.Leave_Management_system.Exception;

// Thrown when employee or leave request is not found in DB

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
