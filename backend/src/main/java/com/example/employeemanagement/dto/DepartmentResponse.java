package com.example.employeemanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentResponse {

    private String id; // Represents departmentCode (e.g. DEPT001) for frontend compatibility

    private Long dbId;

    private String departmentCode;

    private String name;

    private String manager;

    private String managerEmail;

    private String managerPhone;

    private String location;

    private BigDecimal budget;

    private String description;

    private int employeeCount;

    private BigDecimal totalSalary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public DepartmentResponse(String id, Long dbId, String departmentCode, String name,
                              String manager, String managerEmail, String managerPhone,
                              String location, BigDecimal budget, String description,
                              int employeeCount, BigDecimal totalSalary,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.dbId = dbId;
        this.departmentCode = departmentCode;
        this.name = name;
        this.manager = manager;
        this.managerEmail = managerEmail;
        this.managerPhone = managerPhone;
        this.location = location;
        this.budget = budget;
        this.description = description;
        this.employeeCount = employeeCount;
        this.totalSalary = totalSalary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
