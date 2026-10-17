package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Employee> findByDepartmentIgnoreCase(String department);

    List<Employee> findByStatusIgnoreCase(String status);

    List<Employee> findByDepartmentIgnoreCaseAndStatusIgnoreCase(String department, String status);

    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.position) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "e.phone LIKE CONCAT('%', :query, '%')")
    List<Employee> searchEmployees(@Param("query") String query);

    long countByStatusIgnoreCase(String status);

    long countByDepartmentIgnoreCase(String department);
}
