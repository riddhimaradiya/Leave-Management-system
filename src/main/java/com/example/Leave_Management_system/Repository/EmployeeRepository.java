package com.example.Leave_Management_system.Repository;

import com.example.Leave_Management_system.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //SELECT * FROM employees WHERE email = ?
    Optional<Employee> findByEmail(String email);

    // Returns true if any row has this email (used during registration)
    Boolean existsByEmail(String email);
}
