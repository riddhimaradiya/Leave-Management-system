package com.example.Leave_Management_system.service;

import com.example.Leave_Management_system.Repository.EmployeeRepository;
import com.example.Leave_Management_system.Repository.LeaveRequestRepository;
import com.example.Leave_Management_system.model.Employee;
import com.example.Leave_Management_system.model.LeaveRequest;
import com.example.Leave_Management_system.model.LeaveStatus;
import com.example.Leave_Management_system.model.LeaveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveSchedulerService {

    private static final Logger log = (Logger) LoggerFactory.getLogger(LeaveSchedulerService.class);

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    //Auto-cancel PENDING leaves if manager ignores for 3 days
    @Scheduled(cron = "0 0 0 * * ?")//every day at midnight
    @Transactional
    public void autoCancelStalePendingLeaves(){
        log.info("Looking for leaves pending more than 3 days");
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        List<LeaveRequest> oldPendingLeaves = leaveRequestRepository.findByStatusAndAppliedAtBefore(LeaveStatus.PENDING, threeDaysAgo);

        if (oldPendingLeaves.isEmpty()) {
            log.info("No old pending leaves found. Nothing to cancel.");
            return;
        }

        for (LeaveRequest leave : oldPendingLeaves){
            leave.setStatus(LeaveStatus.CANCELLED);
            leave.setRemarks("Auto cancelled because manager did not respond in 3 days.");
            leaveRequestRepository.save(leave);
            giveBackLeaveDays(leave);
            log.info("Cancelled leave ID: {} for employee: {}", leave.getId(),
                    leave.getEmployee().getEmail());
        }
        log.info("Total leaves cancelled: {}", oldPendingLeaves.size());
    }

    //Print a summary of all leaves every morning at 8 AM
    @Scheduled(cron = "0 0 8 * * ?")//run every day at 8:00 AM
    public void printDailySummary(){
        log.info("Daily Leave Summary Report");

        long pending = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        long approved  = leaveRequestRepository.countByStatus(LeaveStatus.APPROVED);
        long rejected  = leaveRequestRepository.countByStatus(LeaveStatus.REJECTED);
        long cancelled = leaveRequestRepository.countByStatus(LeaveStatus.CANCELLED);
        log.info("PENDING   leaves today : {}", pending);
        log.info("APPROVED  leaves today : {}", approved);
        log.info("REJECTED  leaves today : {}", rejected);
        log.info("CANCELLED leaves today : {}", cancelled);
    }

    //restore leave days when a leave is auto-cancelled
    private void giveBackLeaveDays(LeaveRequest leaveRequest){
        Employee employee = leaveRequest.getEmployee();
        int numberOfDays = (int) leaveRequest.getNumberOfDays();
        LeaveType leaveType = leaveRequest.getLeaveType();

        if(leaveType == LeaveType.CASUAL){
            employee.setCasualLeaves(employee.getCasualLeaves() + numberOfDays);
        } else if (leaveType == LeaveType.SICK) {
            employee.setSickLeaves(employee.getSickLeaves()+ numberOfDays);
        } else if (leaveType == LeaveType.EARNED) {
            employee.setEarnedLeaves(employee.getEarnedLeaves() + numberOfDays);
        }

        employeeRepository.save(employee);
    }
}
