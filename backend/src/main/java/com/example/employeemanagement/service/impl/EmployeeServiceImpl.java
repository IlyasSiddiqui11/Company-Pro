package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByIdOrCode(String idOrCode) {
        Employee employee = findEmployeeEntity(idOrCode);
        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Validate unique email
        if (employeeRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("An employee with email '" + request.getEmail() + "' already exists.");
        }

        // Determine employee code
        String code = request.getEmployeeCode();
        if (code == null || code.trim().isEmpty()) {
            code = request.getId();
        }
        if (code == null || code.trim().isEmpty()) {
            code = generateEmployeeCode();
        } else {
            code = code.trim().toUpperCase();
            if (employeeRepository.existsByEmployeeCode(code)) {
                throw new DuplicateResourceException("An employee with code '" + code + "' already exists.");
            }
        }

        Department departmentEntity = null;
        if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
            departmentEntity = departmentRepository.findByNameIgnoreCase(request.getDepartment().trim())
                    .orElse(null);
        }

        Employee employee = Employee.builder()
                .employeeCode(code)
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : "")
                .department(request.getDepartment() != null ? request.getDepartment().trim() : "")
                .departmentEntity(departmentEntity)
                .position(request.getPosition() != null ? request.getPosition().trim() : "")
                .salary(request.getSalary())
                .joiningDate(request.getJoiningDate())
                .status(request.getStatus() != null && !request.getStatus().trim().isEmpty() ? request.getStatus().trim().toLowerCase() : "active")
                .address(request.getAddress() != null ? request.getAddress().trim() : "")
                .emergencyContact(request.getEmergencyContact() != null ? request.getEmergencyContact().trim() : "")
                .bloodGroup(request.getBloodGroup() != null ? request.getBloodGroup().trim() : "")
                .education(request.getEducation() != null ? request.getEducation().trim() : "")
                .build();

        Employee saved = employeeRepository.save(employee);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String idOrCode, EmployeeRequest request) {
        Employee employee = findEmployeeEntity(idOrCode);

        // Validate email uniqueness against other records
        if (employeeRepository.existsByEmailAndIdNot(request.getEmail().trim(), employee.getId())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use by another employee.");
        }

        // Validate employee code uniqueness if changed
        String code = request.getEmployeeCode();
        if (code != null && !code.trim().isEmpty()) {
            code = code.trim().toUpperCase();
            if (employeeRepository.existsByEmployeeCodeAndIdNot(code, employee.getId())) {
                throw new DuplicateResourceException("Employee code '" + code + "' is already in use by another employee.");
            }
            employee.setEmployeeCode(code);
        }

        Department departmentEntity = null;
        if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
            departmentEntity = departmentRepository.findByNameIgnoreCase(request.getDepartment().trim())
                    .orElse(null);
        }

        employee.setName(request.getName().trim());
        employee.setEmail(request.getEmail().trim());
        employee.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");
        employee.setDepartment(request.getDepartment() != null ? request.getDepartment().trim() : "");
        employee.setDepartmentEntity(departmentEntity);
        employee.setPosition(request.getPosition() != null ? request.getPosition().trim() : "");
        employee.setSalary(request.getSalary());
        employee.setJoiningDate(request.getJoiningDate());
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            employee.setStatus(request.getStatus().trim().toLowerCase());
        }
        employee.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        employee.setEmergencyContact(request.getEmergencyContact() != null ? request.getEmergencyContact().trim() : "");
        employee.setBloodGroup(request.getBloodGroup() != null ? request.getBloodGroup().trim() : "");
        employee.setEducation(request.getEducation() != null ? request.getEducation().trim() : "");

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteEmployee(String idOrCode) {
        Employee employee = findEmployeeEntity(idOrCode);
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeRepository.searchEmployees(query.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> filterEmployees(String department, String status) {
        boolean hasDept = department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("all");
        boolean hasStatus = status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("all");

        List<Employee> result;
        if (hasDept && hasStatus) {
            result = employeeRepository.findByDepartmentIgnoreCaseAndStatusIgnoreCase(department.trim(), status.trim());
        } else if (hasDept) {
            result = employeeRepository.findByDepartmentIgnoreCase(department.trim());
        } else if (hasStatus) {
            result = employeeRepository.findByStatusIgnoreCase(status.trim());
        } else {
            result = employeeRepository.findAll();
        }

        return result.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private Employee findEmployeeEntity(String idOrCode) {
        if (idOrCode == null || idOrCode.trim().isEmpty()) {
            throw new ResourceNotFoundException("Employee identifier is missing.");
        }
        String clean = idOrCode.trim();

        // Try by employeeCode first
        Optional<Employee> byCode = employeeRepository.findByEmployeeCode(clean.toUpperCase());
        if (byCode.isPresent()) {
            return byCode.get();
        }

        // Try numeric ID
        try {
            Long numericId = Long.parseLong(clean);
            return employeeRepository.findById(numericId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id or code: " + clean));
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Employee not found with code: " + clean);
        }
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count();
        long nextIndex = count + 1;
        String code = String.format("EMP%03d", nextIndex);
        while (employeeRepository.existsByEmployeeCode(code)) {
            nextIndex++;
            code = String.format("EMP%03d", nextIndex);
        }
        return code;
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getEmployeeCode()) // Matches frontend expectation: employee.id === 'EMP001'
                .dbId(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .name(emp.getName())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .department(emp.getDepartment())
                .position(emp.getPosition())
                .salary(emp.getSalary())
                .joiningDate(emp.getJoiningDate())
                .status(emp.getStatus())
                .address(emp.getAddress())
                .emergencyContact(emp.getEmergencyContact())
                .bloodGroup(emp.getBloodGroup())
                .education(emp.getEducation())
                .createdAt(emp.getCreatedAt())
                .updatedAt(emp.getUpdatedAt())
                .build();
    }
}
