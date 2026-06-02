package com.example.Leave_Management_system.Exception;


// Thrown when an employee doesn't have enough leave balance
public class InsufficientLeaveException extends RuntimeException {
    public InsufficientLeaveException(String message){
        super(message);
    }

}
