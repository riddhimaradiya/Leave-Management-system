package com.example.Leave_Management_system.DTO;

import com.example.Leave_Management_system.model.LeaveRequest;
import com.example.Leave_Management_system.model.LeaveStatus;
import com.example.Leave_Management_system.model.LeaveType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class LeaveResponse {
    private Long id;
    private String employeeName;
    private String employeeEmail;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private long numberOfDays;
    private String reason;
    private String remarks;
    private LeaveStatus status;
    private LocalDateTime appliedAt;

    public static LeaveResponse fromEntity(LeaveRequest entity) {
        LeaveResponse response = new LeaveResponse();
        response.setId(entity.getId());
        response.setEmployeeName(entity.getEmployee().getName());
        response.setEmployeeEmail(entity.getEmployee().getEmail());
        response.setLeaveType(entity.getLeaveType());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setNumberOfDays(entity.getNumberOfDays());
        response.setReason(entity.getReason());
        response.setRemarks(entity.getRemarks());
        response.setStatus(entity.getStatus());
        response.setAppliedAt(entity.getAppliedAt());
        return response;
    }
}
