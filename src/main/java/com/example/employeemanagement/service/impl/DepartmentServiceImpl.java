package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.DepartmentRequest;
import com.example.employeemanagement.dto.DepartmentResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentByIdOrCode(String idOrCode) {
        Department dept = findDepartmentEntity(idOrCode);
        return mapToResponse(dept);
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Department with name '" + request.getName() + "' already exists.");
        }

        String code = request.getDepartmentCode();
        if (code == null || code.trim().isEmpty()) {
            code = request.getId();
        }
        if (code == null || code.trim().isEmpty()) {
            code = generateDepartmentCode();
        } else {
            code = code.trim().toUpperCase();
            if (departmentRepository.existsByDepartmentCode(code)) {
                throw new DuplicateResourceException("Department with code '" + code + "' already exists.");
            }
        }

        Department department = Department.builder()
                .departmentCode(code)
                .name(request.getName().trim())
                .manager(request.getManager().trim())
                .managerEmail(request.getManagerEmail().trim())
                .managerPhone(request.getManagerPhone().trim())
                .location(request.getLocation().trim())
                .budget(request.getBudget())
                .description(request.getDescription() != null ? request.getDescription().trim() : "")
                .build();

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(String idOrCode, DepartmentRequest request) {
        Department dept = findDepartmentEntity(idOrCode);

        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), dept.getId())) {
            throw new DuplicateResourceException("Department name '" + request.getName() + "' is already used by another department.");
        }

        String oldName = dept.getName();
        String newName = request.getName().trim();

        dept.setName(newName);
        dept.setManager(request.getManager().trim());
        dept.setManagerEmail(request.getManagerEmail().trim());
        dept.setManagerPhone(request.getManagerPhone().trim());
        dept.setLocation(request.getLocation().trim());
        dept.setBudget(request.getBudget());
        dept.setDescription(request.getDescription() != null ? request.getDescription().trim() : "") ;

        Department updated = departmentRepository.save(dept);

        // If department name was changed, update employees in this department as well
        if (!oldName.equalsIgnoreCase(newName)) {
            List<Employee> employees = employeeRepository.findByDepartmentIgnoreCase(oldName);
            for (Employee emp : employees) {
                emp.setDepartment(newName);
                employeeRepository.save(emp);
            }
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDepartment(String idOrCode) {
        Department dept = findDepartmentEntity(idOrCode);
        departmentRepository.delete(dept);
    }

    private Department findDepartmentEntity(String idOrCode) {
        if (idOrCode == null || idOrCode.trim().isEmpty()) {
            throw new ResourceNotFoundException("Department identifier is missing.");
        }
        String clean = idOrCode.trim();

        Optional<Department> byCode = departmentRepository.findByDepartmentCode(clean.toUpperCase());
        if (byCode.isPresent()) {
            return byCode.get();
        }

        Optional<Department> byName = departmentRepository.findByNameIgnoreCase(clean);
        if (byName.isPresent()) {
            return byName.get();
        }

        try {
            Long numericId = Long.parseLong(clean);
            return departmentRepository.findById(numericId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id or code: " + clean));
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Department not found with code: " + clean);
        }
    }

    private String generateDepartmentCode() {
        long count = departmentRepository.count();
        long nextIndex = count + 1;
        String code = String.format("DEPT%03d", nextIndex);
        while (departmentRepository.existsByDepartmentCode(code)) {
            nextIndex++;
            code = String.format("DEPT%03d", nextIndex);
        }
        return code;
    }

    private DepartmentResponse mapToResponse(Department dept) {
        List<Employee> deptEmployees = employeeRepository.findByDepartmentIgnoreCase(dept.getName());
        int count = deptEmployees.size();
        BigDecimal totalSalary = deptEmployees.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DepartmentResponse.builder()
                .id(dept.getDepartmentCode()) // Matches frontend expectation: dept.id === 'DEPT001'
                .dbId(dept.getId())
                .departmentCode(dept.getDepartmentCode())
                .name(dept.getName())
                .manager(dept.getManager())
                .managerEmail(dept.getManagerEmail())
                .managerPhone(dept.getManagerPhone())
                .location(dept.getLocation())
                .budget(dept.getBudget())
                .description(dept.getDescription())
                .employeeCount(count)
                .totalSalary(totalSalary)
                .createdAt(dept.getCreatedAt())
                .updatedAt(dept.getUpdatedAt())
                .build();
    }
}
