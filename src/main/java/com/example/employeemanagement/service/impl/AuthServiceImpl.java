package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.AuthResponse;
import com.example.employeemanagement.dto.LoginRequest;
import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.UserRepository;
import com.example.employeemanagement.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .success(true)
                .message("Login successful")
                .username(user.getUsername())
                .name(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return AuthResponse.builder()
                .success(true)
                .username(user.getUsername())
                .name(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
