package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "role", length = 30)
    private String role = "ADMIN";

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Builder
    public AppUser(Long id, String username, String password, String fullName,
                   String email, String role, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = (role != null) ? role : "ADMIN";
        this.lastLogin = lastLogin;
    }
}
