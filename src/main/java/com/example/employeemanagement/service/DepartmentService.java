package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.DepartmentRequest;
import com.example.employeemanagement.dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentByIdOrCode(String idOrCode);

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse updateDepartment(String idOrCode, DepartmentRequest request);

    void deleteDepartment(String idOrCode);
}
