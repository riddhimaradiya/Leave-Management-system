package com.example.Leave_Management_system.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; // CASUAL/SICK/EARNED

    private LocalDate StartDate;
    private LocalDate EndDate;

    private String reason;
    private String remarks;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;

    // When was this request submitted
    private LocalDateTime appliedAt = LocalDateTime.now();

    public long getNumberOfDays(){
        return StartDate.datesUntil(EndDate.plusDays(1)).count();
    }
}
