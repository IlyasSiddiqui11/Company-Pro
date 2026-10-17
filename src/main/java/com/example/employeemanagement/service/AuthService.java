package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.AuthResponse;
import com.example.employeemanagement.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse getCurrentUser(String username);
}
