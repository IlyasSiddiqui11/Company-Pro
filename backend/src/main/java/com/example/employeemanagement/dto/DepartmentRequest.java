package com.example.employeemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentRequest {

    private String id; // Code or ID if passed

    private String departmentCode;

    @NotBlank(message = "Department name is required")
    private String name;

    @NotBlank(message = "Manager name is required")
    private String manager;

    @NotBlank(message = "Manager email is required")
    @Email(message = "Manager email must be valid")
    private String managerEmail;

    @NotBlank(message = "Manager phone is required")
    private String managerPhone;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Budget is required")
    @PositiveOrZero(message = "Budget must be non-negative")
    private BigDecimal budget;

    private String description;

    @Builder
    public DepartmentRequest(String id, String departmentCode, String name, String manager,
                             String managerEmail, String managerPhone, String location,
                             BigDecimal budget, String description) {
        this.id = id;
        this.departmentCode = departmentCode;
        this.name = name;
        this.manager = manager;
        this.managerEmail = managerEmail;
        this.managerPhone = managerPhone;
        this.location = location;
        this.budget = budget;
        this.description = description;
    }
}
