package com.example.Leave_Management_system.Controller;

import com.example.Leave_Management_system.DTO.LeaveApplyRequest;
import com.example.Leave_Management_system.DTO.LeaveResponse;
import com.example.Leave_Management_system.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/apply")
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveApplyRequest request){
        LeaveResponse response = leaveService.applyLeave(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long id){
        LeaveResponse response = leaveService.approveLeave(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks){
        LeaveResponse response = leaveService.rejectLeave(id, remarks);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<LeaveResponse> cancelLeave(@PathVariable Long id) {
        LeaveResponse response = leaveService.cancelLeave(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<LeaveResponse>> getMyLeaves(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LeaveResponse> leavePage = leaveService.getMyLeave(page, size);
        return ResponseEntity.ok(leavePage);
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> getLeaveBalance() {
        return ResponseEntity.ok(leaveService.getLeaveBalance());
    }
}
