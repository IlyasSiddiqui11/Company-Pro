package com.example.employeemanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeResponse {

    private String id; // Represents employeeCode (e.g. EMP001) for 100% frontend compatibility

    private Long dbId; // Numeric DB primary key

    private String employeeCode;

    private String name;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String department;

    private String position;

    private BigDecimal salary;

    private LocalDate joiningDate;

    private String status;

    private String address;

    private String emergencyContact;

    private String bloodGroup;

    private String education;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public EmployeeResponse(String id, Long dbId, String employeeCode, String name,
                            String firstName, String lastName, String email, String phone,
                            String department, String position, BigDecimal salary,
                            LocalDate joiningDate, String status, String address,
                            String emergencyContact, String bloodGroup, String education,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.dbId = dbId;
        this.employeeCode = employeeCode;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
