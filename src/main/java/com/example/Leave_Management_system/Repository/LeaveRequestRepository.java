package com.example.Leave_Management_system.Repository;

import com.example.Leave_Management_system.model.Employee;
import com.example.Leave_Management_system.model.LeaveRequest;
import com.example.Leave_Management_system.model.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByEmployee(Employee employee, Pageable pageable);

    //Get all leave requests for one employee filtered by status
    Page<LeaveRequest> findByEmployeeAndStatus(Employee employee, LeaveStatus status, Pageable pageable);

    List<LeaveRequest> findByStatusAndAppliedAtBefore(LeaveStatus status, LocalDateTime cutoff);

    //used for daily summary report
    long countByStatus(LeaveStatus status);
}
