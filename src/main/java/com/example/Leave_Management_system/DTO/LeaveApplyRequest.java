package com.example.Leave_Management_system.DTO;

import com.example.Leave_Management_system.model.LeaveType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplyRequest {
    @NotNull(message = "leave type is required")
    public LeaveType leaveType;

    @NotNull(message = "Start date is required")
    public LocalDate startDate;

    @NotNull(message = "End date is required")
    public LocalDate endDate;

    @NotBlank(message = "Reason is required")
    public String reason;
}
