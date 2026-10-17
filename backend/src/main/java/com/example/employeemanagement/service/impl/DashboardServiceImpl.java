package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.DashboardStatsResponse;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.service.DashboardService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    private static final Map<String, String> DEPT_ICONS = new HashMap<>();

    static {
        DEPT_ICONS.put("Engineering", "fa-code");
        DEPT_ICONS.put("IT", "fa-laptop-code");
        DEPT_ICONS.put("Marketing", "fa-bullhorn");
        DEPT_ICONS.put("Sales", "fa-chart-line");
        DEPT_ICONS.put("HR", "fa-users");
        DEPT_ICONS.put("Finance", "fa-dollar-sign");
        DEPT_ICONS.put("Operations", "fa-cogs");
    }

    public DashboardServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalEmployees = employeeRepository.count();
        long totalDepartments = departmentRepository.count();
        long presentToday = employeeRepository.countByStatusIgnoreCase("active");
        long onLeave = employeeRepository.countByStatusIgnoreCase("on-leave");

        double attendanceRate = 0.0;
        double leavePercentage = 0.0;

        if (totalEmployees > 0) {
            attendanceRate = BigDecimal.valueOf((double) presentToday / totalEmployees * 100.0)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            leavePercentage = BigDecimal.valueOf((double) onLeave / totalEmployees * 100.0)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        List<Department> departments = departmentRepository.findAll();
        List<DashboardStatsResponse.DepartmentStatDto> deptOverview = departments.stream().map(dept -> {
            int count = employeeRepository.findByDepartmentIgnoreCase(dept.getName()).size();
            String icon = DEPT_ICONS.getOrDefault(dept.getName(), "fa-building");
            return DashboardStatsResponse.DepartmentStatDto.builder()
                    .name(dept.getName())
                    .count(count)
                    .icon(icon)
                    .build();
        }).collect(Collectors.toList());

        List<EmployeeResponse> recentEmployees = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .limit(6)
                .map(this::mapEmployeeToResponse)
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalEmployees(totalEmployees)
                .totalDepartments(totalDepartments)
                .presentToday(presentToday)
                .onLeave(onLeave)
                .attendanceRate(attendanceRate)
                .leavePercentage(leavePercentage)
                .departmentOverview(deptOverview)
                .recentEmployees(recentEmployees)
                .build();
    }

    private EmployeeResponse mapEmployeeToResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getEmployeeCode())
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
                .createdAt(emp.getCreatedAt())
                .updatedAt(emp.getUpdatedAt())
                .build();
    }
}
