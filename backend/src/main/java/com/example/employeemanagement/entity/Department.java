package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_code", unique = true, nullable = false, length = 20)
    private String departmentCode;

    @NotBlank(message = "Department name is required")
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "manager", length = 100)
    private String manager;

    @Column(name = "manager_email", length = 100)
    private String managerEmail;

    @Column(name = "manager_phone", length = 30)
    private String managerPhone;

    @Column(name = "location", length = 200)
    private String location;

    @PositiveOrZero(message = "Budget must be non-negative")
    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "departmentEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Department(Long id, String departmentCode, String name, String manager,
                      String managerEmail, String managerPhone, String location,
                      BigDecimal budget, String description, List<Employee> employees,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.departmentCode = departmentCode;
        this.name = name;
        this.manager = manager;
        this.managerEmail = managerEmail;
        this.managerPhone = managerPhone;
        this.location = location;
        this.budget = budget;
        this.description = description;
        this.employees = (employees != null) ? employees : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
