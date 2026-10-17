package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeResponse sampleEmployee;
    private EmployeeRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleEmployee = EmployeeResponse.builder()
                .id("EMP001")
                .employeeCode("EMP001")
                .name("John Doe")
                .email("john.doe@company.com")
                .phone("+91 9876543210")
                .department("Engineering")
                .position("Senior Developer")
                .salary(BigDecimal.valueOf(85000))
                .joiningDate(LocalDate.of(2022, 1, 15))
                .status("active")
                .build();

        validRequest = EmployeeRequest.builder()
                .name("John Doe")
                .email("john.doe@company.com")
                .phone("+91 9876543210")
                .department("Engineering")
                .position("Senior Developer")
                .salary(BigDecimal.valueOf(85000))
                .joiningDate(LocalDate.of(2022, 1, 15))
                .status("active")
                .build();
    }

    @Test
    @DisplayName("GET /api/employees returns list of employees")
    void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(sampleEmployee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeCode").value("EMP001"))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@company.com"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} returns single employee")
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeByIdOrCode("EMP001")).thenReturn(sampleEmployee);

        mockMvc.perform(get("/api/employees/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @DisplayName("POST /api/employees creates employee with status 201")
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(sampleEmployee);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @DisplayName("POST /api/employees rejects negative or zero salary with status 400")
    void testCreateEmployeeInvalidSalary() throws Exception {
        validRequest.setSalary(BigDecimal.valueOf(-500));

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.salary").exists());
    }

    @Test
    @DisplayName("PUT /api/employees/{id} updates employee")
    void testUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(eq("EMP001"), any(EmployeeRequest.class))).thenReturn(sampleEmployee);

        mockMvc.perform(put("/api/employees/EMP001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode").value("EMP001"));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} deletes employee with status 204")
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee("EMP001");

        mockMvc.perform(delete("/api/employees/EMP001"))
                .andExpect(status().isNoContent());
    }
}
