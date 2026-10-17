package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee code is required")
    @Column(name = "employee_code", unique = true, nullable = false, length = 30)
    private String employeeCode;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "department_name", length = 100)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department departmentEntity;

    @Column(name = "position", length = 100)
    private String position;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Salary format is invalid")
    @Column(name = "salary", precision = 12, scale = 2, nullable = false)
    private BigDecimal salary;

    @NotNull(message = "Joining date is required")
    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "status", length = 30)
    private String status = "active";

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "emergency_contact", length = 30)
    private String emergencyContact;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "education", length = 100)
    private String education;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Employee(Long id, String employeeCode, String name, String firstName, String lastName,
                    String email, String phone, String department, Department departmentEntity,
                    String position, BigDecimal salary, LocalDate joiningDate, String status,
                    String address, String emergencyContact, String bloodGroup, String education,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.departmentEntity = departmentEntity;
        this.position = position;
        this.salary = salary;
        this.joiningDate = joiningDate;
        this.status = (status != null) ? status : "active";
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.bloodGroup = bloodGroup;
        this.education = education;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        syncNames();
    }

    @PrePersist
    @PreUpdate
    public void syncNames() {
        if (this.name != null && !this.name.trim().isEmpty()) {
            String[] parts = this.name.trim().split("\\s+", 2);
            this.firstName = parts[0];
            this.lastName = parts.length > 1 ? parts[1] : "";
        }
    }
}
