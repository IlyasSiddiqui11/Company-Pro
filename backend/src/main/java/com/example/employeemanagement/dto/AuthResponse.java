package com.example.employeemanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private boolean success;
    private String message;
    private String username;
    private String name;
    private String email;
    private String role;

    @Builder
    public AuthResponse(boolean success, String message, String username, String name, String email, String role) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
