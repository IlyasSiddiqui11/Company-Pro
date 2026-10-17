package com.example.employeemanagement.config;

import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdminUser();
        initDepartments();
        initEmployees();
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Admin User")
                    .email("admin@companypro.com")
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
            log.info("Initialized default admin user: admin / admin123");
        }
    }

    private void initDepartments() {
        if (departmentRepository.count() == 0) {
            List<Department> departments = List.of(
                Department.builder()
                    .departmentCode("DEPT001")
                    .name("Engineering")
                    .manager("John Doe")
                    .managerEmail("john.doe@company.com")
                    .managerPhone("+91 9876543210")
                    .budget(BigDecimal.valueOf(5000000))
                    .location("Building A, Floor 3")
                    .description("Software development and technical operations")
                    .build(),
                Department.builder()
                    .departmentCode("DEPT002")
                    .name("Marketing")
                    .manager("Jane Smith")
                    .managerEmail("jane.smith@company.com")
                    .managerPhone("+91 9876543220")
                    .budget(BigDecimal.valueOf(3000000))
                    .location("Building B, Floor 2")
                    .description("Brand management and market strategy")
                    .build(),
                Department.builder()
                    .departmentCode("DEPT003")
                    .name("Sales")
                    .manager("Mike Johnson")
                    .managerEmail("mike.johnson@company.com")
                    .managerPhone("+91 9876543230")
                    .budget(BigDecimal.valueOf(2500000))
                    .location("Building A, Floor 1")
                    .description("Sales operations and customer relations")
                    .build(),
                Department.builder()
                    .departmentCode("DEPT004")
                    .name("HR")
                    .manager("Sarah Williams")
                    .managerEmail("sarah.williams@company.com")
                    .managerPhone("+91 9876543240")
                    .budget(BigDecimal.valueOf(1500000))
                    .location("Building B, Floor 1")
                    .description("Human resources and talent management")
                    .build(),
                Department.builder()
                    .departmentCode("DEPT005")
                    .name("Finance")
                    .manager("Robert Brown")
                    .managerEmail("robert.brown@company.com")
                    .managerPhone("+91 9876543250")
                    .budget(BigDecimal.valueOf(2000000))
                    .location("Building A, Floor 2")
                    .description("Financial planning and accounting")
                    .build()
            );

            departmentRepository.saveAll(departments);
            log.info("Initialized {} default departments", departments.size());
        }
    }

    private void initEmployees() {
        if (employeeRepository.count() == 0) {
            Department eng = departmentRepository.findByNameIgnoreCase("Engineering").orElse(null);
            Department mkt = departmentRepository.findByNameIgnoreCase("Marketing").orElse(null);
            Department sales = departmentRepository.findByNameIgnoreCase("Sales").orElse(null);
            Department hr = departmentRepository.findByNameIgnoreCase("HR").orElse(null);
            Department fin = departmentRepository.findByNameIgnoreCase("Finance").orElse(null);

            List<Employee> employees = List.of(
                Employee.builder()
                    .employeeCode("EMP001")
                    .name("John Doe")
                    .email("john.doe@company.com")
                    .phone("+91 9876543210")
                    .department("Engineering")
                    .departmentEntity(eng)
                    .position("Senior Developer")
                    .salary(BigDecimal.valueOf(85000))
                    .joiningDate(LocalDate.of(2022, 1, 15))
                    .status("active")
                    .address("123 Main Street, New Delhi")
                    .emergencyContact("+91 9876543211")
                    .bloodGroup("O+")
                    .education("B.Tech Computer Science")
                    .build(),
                Employee.builder()
                    .employeeCode("EMP002")
                    .name("Jane Smith")
                    .email("jane.smith@company.com")
                    .phone("+91 9876543220")
                    .department("Marketing")
                    .departmentEntity(mkt)
                    .position("Marketing Manager")
                    .salary(BigDecimal.valueOf(75000))
                    .joiningDate(LocalDate.of(2022, 3, 20))
                    .status("active")
                    .address("456 Park Avenue, Mumbai")
                    .emergencyContact("+91 9876543221")
                    .bloodGroup("A+")
                    .education("MBA Marketing")
                    .build(),
                Employee.builder()
                    .employeeCode("EMP003")
                    .name("Mike Johnson")
                    .email("mike.johnson@company.com")
                    .phone("+91 9876543230")
                    .department("Sales")
                    .departmentEntity(sales)
                    .position("Sales Executive")
                    .salary(BigDecimal.valueOf(65000))
                    .joiningDate(LocalDate.of(2022, 5, 10))
                    .status("active")
                    .address("789 Lake View, Bangalore")
                    .emergencyContact("+91 9876543231")
                    .bloodGroup("B+")
                    .education("BBA")
                    .build(),
                Employee.builder()
                    .employeeCode("EMP004")
                    .name("Sarah Williams")
                    .email("sarah.williams@company.com")
                    .phone("+91 9876543240")
                    .department("HR")
                    .departmentEntity(hr)
                    .position("HR Specialist")
                    .salary(BigDecimal.valueOf(60000))
                    .joiningDate(LocalDate.of(2022, 7, 1))
                    .status("active")
                    .address("321 Garden Road, Pune")
                    .emergencyContact("+91 9876543241")
                    .bloodGroup("AB+")
                    .education("MBA HR")
                    .build(),
                Employee.builder()
                    .employeeCode("EMP005")
                    .name("Robert Brown")
                    .email("robert.brown@company.com")
                    .phone("+91 9876543250")
                    .department("Finance")
                    .departmentEntity(fin)
                    .position("Accountant")
                    .salary(BigDecimal.valueOf(70000))
                    .joiningDate(LocalDate.of(2021, 12, 15))
                    .status("on-leave")
                    .address("654 Hill Station, Chennai")
                    .emergencyContact("+91 9876543251")
                    .bloodGroup("O-")
                    .education("B.Com, CA")
                    .build(),
                Employee.builder()
                    .employeeCode("EMP006")
                    .name("Emily Davis")
                    .email("emily.davis@company.com")
                    .phone("+91 9876543260")
                    .department("Engineering")
                    .departmentEntity(eng)
                    .position("Junior Developer")
                    .salary(BigDecimal.valueOf(55000))
                    .joiningDate(LocalDate.of(2023, 2, 1))
                    .status("active")
                    .address("987 Beach Road, Goa")
                    .emergencyContact("+91 9876543261")
                    .bloodGroup("A-")
                    .education("B.Tech IT")
                    .build()
            );

            employeeRepository.saveAll(employees);
            log.info("Initialized {} default employees", employees.size());
        }
    }
}
