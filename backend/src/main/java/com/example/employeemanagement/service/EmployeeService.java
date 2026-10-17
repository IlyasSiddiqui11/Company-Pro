package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeByIdOrCode(String idOrCode);

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse updateEmployee(String idOrCode, EmployeeRequest request);

    void deleteEmployee(String idOrCode);

    List<EmployeeResponse> searchEmployees(String query);

    List<EmployeeResponse> filterEmployees(String department, String status);
}
