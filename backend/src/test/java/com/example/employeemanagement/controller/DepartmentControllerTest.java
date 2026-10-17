package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.DepartmentRequest;
import com.example.employeemanagement.dto.DepartmentResponse;
import com.example.employeemanagement.service.DepartmentService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentResponse sampleDepartment;
    private DepartmentRequest validRequest;

    @BeforeEach
    void setUp() {
        sampleDepartment = DepartmentResponse.builder()
                .id("DEPT001")
                .departmentCode("DEPT001")
                .name("Engineering")
                .manager("John Doe")
                .managerEmail("john.doe@company.com")
                .managerPhone("+91 9876543210")
                .location("Building A, Floor 3")
                .budget(BigDecimal.valueOf(5000000))
                .employeeCount(2)
                .build();

        validRequest = DepartmentRequest.builder()
                .name("Engineering")
                .manager("John Doe")
                .managerEmail("john.doe@company.com")
                .managerPhone("+91 9876543210")
                .location("Building A, Floor 3")
                .budget(BigDecimal.valueOf(5000000))
                .description("Software Development")
                .build();
    }

    @Test
    @DisplayName("GET /api/departments returns department list")
    void testGetAllDepartments() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(List.of(sampleDepartment));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departmentCode").value("DEPT001"))
                .andExpect(jsonPath("$[0].name").value("Engineering"));
    }

    @Test
    @DisplayName("POST /api/departments creates department with 201")
    void testCreateDepartment() throws Exception {
        when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(sampleDepartment);

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentCode").value("DEPT001"))
                .andExpect(jsonPath("$.name").value("Engineering"));
    }
}
