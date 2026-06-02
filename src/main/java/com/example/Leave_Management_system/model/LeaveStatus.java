package com.example.Leave_Management_system.model;

// A leave request moves through these statuses
// PENDING - APPROVED or REJECTED
// PENDING - CANCELLED (by scheduler after 3 days, or by employee)
public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
