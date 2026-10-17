package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByNameIgnoreCase(String name);

    Optional<Department> findByDepartmentCode(String departmentCode);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByDepartmentCode(String departmentCode);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByDepartmentCodeAndIdNot(String departmentCode, Long id);
}
