package com.example.employeemanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRequest {

    private String id; // In case passed from frontend

    private String employeeCode;

    @NotBlank(message = "Employee full name is required")
    private String name;

    @NotBlank(message = "Email address is required")
    @Email(message = "Email address must be a valid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.01", message = "Salary must be greater than zero")
    private BigDecimal salary;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private String status;

    private String address;

    private String emergencyContact;

    private String bloodGroup;

    private String education;

    @Builder
    public EmployeeRequest(String id, String employeeCode, String name, String email, String phone,
                           String department, String position, BigDecimal salary,
                           LocalDate joiningDate, String status, String address,
                           String emergencyContact, String bloodGroup, String education) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.position = position;
        this.salary = salary;
        this.joiningDate = joiningDate;
        this.status = status;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.bloodGroup = bloodGroup;
        this.education = education;
    }
}
