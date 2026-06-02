package com.example.Leave_Management_system.service;

import com.example.Leave_Management_system.DTO.LoginRequest;
import com.example.Leave_Management_system.DTO.RegisterRequest;
import com.example.Leave_Management_system.JWTSecurity.JwtUtill;
import com.example.Leave_Management_system.Repository.EmployeeRepository;
import com.example.Leave_Management_system.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtill jwtUtill;

    public Map<String, String> register(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
            Employee employee = new Employee();
            employee.setName(request.getName());
            employee.setEmail(request.getEmail());
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
            employee.setRole(request.getRole());
            employeeRepository.save(employee);
            return Map.of("message", "Employee registered successfully!");
    }
    public Map<String , String> login(LoginRequest request){
        // This checks email,password against the database
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtill.generateToken(request.getEmail());
        return Map.of("token", token);
    }
}
