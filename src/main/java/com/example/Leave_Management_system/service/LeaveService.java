package com.example.Leave_Management_system.service;

import com.example.Leave_Management_system.DTO.LeaveApplyRequest;
import com.example.Leave_Management_system.DTO.LeaveResponse;
import com.example.Leave_Management_system.Exception.InsufficientLeaveException;
import com.example.Leave_Management_system.Exception.ResourceNotFoundException;
import com.example.Leave_Management_system.Exception.UnauthorizedActionException;
import com.example.Leave_Management_system.Repository.EmployeeRepository;
import com.example.Leave_Management_system.Repository.LeaveRequestRepository;
import com.example.Leave_Management_system.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationService notificationService;

    //get the currently logged-in employee from JWT
    private Employee getCurrentEmployee() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + email));
    }

    //Apply for leave
    @Transactional
    public LeaveResponse applyLeave(LeaveApplyRequest request){
        Employee employee = getCurrentEmployee();

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        //calculate days
        long days = request.getStartDate().datesUntil(request.getEndDate().plusDays(1)).count();

        checkAndDeductLeaveBalance(employee, request.getLeaveType(), (int) days);

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        notificationService.sendAppliedNotification(
                employee.getName(),
                "manager@company.com",
                (int) saved.getNumberOfDays()
        );

        return LeaveResponse.fromEntity(saved);
    }

    //Check employee enough balance, then deducts
    private void checkAndDeductLeaveBalance(Employee employee, LeaveType type, int days){
        switch (type){
            case CASUAL -> {
                if (employee.getCasualLeaves() < days)
                    throw new InsufficientLeaveException("not enough casual leaves. Available: " + employee.getCasualLeaves());
                employee.setCasualLeaves(employee.getCasualLeaves() - days);
            }
            case SICK -> {
                if (employee.getSickLeaves() < days)
                    throw new InsufficientLeaveException("Not enough sick leaves. Available: " + employee.getSickLeaves());
                employee.setSickLeaves(employee.getSickLeaves() - days);
            }
            case EARNED -> {
                if (employee.getEarnedLeaves() < days)
                    throw new InsufficientLeaveException("Not enough earned leaves. Available: " + employee.getEarnedLeaves());
                employee.setEarnedLeaves(employee.getEarnedLeaves() - days);
            }
        }
        employeeRepository.save(employee);
    }

    //Approve a Leave request(Manager only)
    @Transactional
    public LeaveResponse approveLeave(Long leaveId){
        Employee manager = getCurrentEmployee();

        if (manager.getRole() == Role.EMPLOYEE){
            throw new UnauthorizedActionException("Only manager can approve leave requests.");
        }
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));


        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING requests can be approved.");
        }
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setRemarks("Approved by " + manager.getName());
        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        notificationService.sendLeaveApprovedNotification(
                leaveRequest.getEmployee().getName(),
                leaveRequest.getEmployee().getEmail(),
                (int) leaveRequest.getNumberOfDays()
        );
        return LeaveResponse.fromEntity(updated);
    }

    //Reject a leave request(Manager Only)
    @Transactional
    public LeaveResponse rejectLeave(Long leaveId, String remarks){
        Employee manager = getCurrentEmployee();
        if (manager.getRole() == Role.EMPLOYEE) {
            throw new UnauthorizedActionException("Only managers can reject leave requests.");
        }

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING requests can be rejected.");
        }

        // Restore the leave balance since the leave is rejected
        restoreLeaveBalance(leaveRequest.getEmployee(),
                leaveRequest.getLeaveType(),
                (int) leaveRequest.getNumberOfDays());
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setRemarks(remarks != null ? remarks : " Rejected by " + manager.getName());

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        notificationService.sendRejectedNotification(
                leaveRequest.getEmployee().getName(),
                leaveRequest.getEmployee().getEmail(),
                remarks
        );

        return LeaveResponse.fromEntity(updated);    }

    //Cancel a Leave (employee can cancel own pending leave)
    @Transactional
    public LeaveResponse cancelLeave(Long leaveId){
        Employee employee = getCurrentEmployee();

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));

        if (!leaveRequest.getEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedActionException("You can only cancel your own leave requests.");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING requests can be cancelled.");
        }

        restoreLeaveBalance(employee, leaveRequest.getLeaveType(),
                (int) leaveRequest.getNumberOfDays());
        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setRemarks("cancelled by employee.");

        return LeaveResponse.fromEntity(leaveRequestRepository.save(leaveRequest));
    }

    //Restores leave balance when leave is rejected or cancelled
    private void restoreLeaveBalance(Employee employee, LeaveType type, int days){
        switch (type){
            case CASUAL -> employee.setCasualLeaves(employee.getCasualLeaves() + days);
            case SICK    -> employee.setSickLeaves(employee.getSickLeaves() + days);
            case EARNED  -> employee.setEarnedLeaves(employee.getEarnedLeaves() + days);
        }
        employeeRepository.save(employee);
    }

    //Pagination(Get paginated leave history for the logged-in employee)
    public Page<LeaveResponse> getMyLeave(int page, int size){
        Employee employee = getCurrentEmployee();

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<LeaveRequest> leavePage = leaveRequestRepository.findByEmployee(employee, pageable);

        // Convert each LeaveRequest entity to LeaveResponse DTO
        return leavePage.map(LeaveResponse::fromEntity);
    }

    //get leave balance for current employee
    public Object getLeaveBalance() {
        Employee employee = getCurrentEmployee();
        return java.util.Map.of(
                "name",           employee.getName(),
                "casualLeaves",   employee.getCasualLeaves(),
                "sickLeaves",     employee.getSickLeaves(),
                "earnedLeaves",   employee.getEarnedLeaves()
        );
    }
}
