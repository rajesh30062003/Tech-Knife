package com.techknife.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.employee.dto.CreateEmployeeRequest;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.dto.UpdateEmployeeRequest;
import com.techknife.employee.dto.UpdateEmployeeStatusRequest;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmploymentType;

import com.techknife.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.techknife.backend.TechKnifeBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean(name = "employeeFeatureServiceImpl")
    private EmployeeService employeeService;



    private EmployeeResponse sampleResponse;
    private CreateEmployeeRequest createRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = EmployeeResponse.builder()
                .id("emp-doc-1001")
                .employeeId("EMP-1001")
                .officialEmail("sarah.connor@techknife.com")
                .firstName("Sarah")
                .lastName("Connor")
                .fullName("Sarah Connor")
                .departmentId("Engineering & DevOps")
                .designationId("Chief Technology Officer")
                .salary(java.math.BigDecimal.valueOf(210000.0))
                .status(EmployeeStatus.ACTIVE)
                .joiningDate(java.time.LocalDate.parse("2021-06-01"))
                .build();


        createRequest = new CreateEmployeeRequest();
        createRequest.setEmployeeId("EMP-1001");
        createRequest.setOfficialEmail("sarah.connor@techknife.com");
        createRequest.setFirstName("Sarah");
        createRequest.setLastName("Connor");
        createRequest.setPrimaryMobile("+15550189921");
        createRequest.setDepartmentId("Engineering & DevOps");
        createRequest.setDesignationId("Chief Technology Officer");
        createRequest.setJoiningDate(java.time.LocalDate.parse("2021-06-01"));
        createRequest.setEmploymentType(EmploymentType.FULL_TIME);
        createRequest.setSalary(java.math.BigDecimal.valueOf(210000.0));
    }



    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /api/v2/employees - Onboard new employee (HTTP 201 Created)")
    void createEmployee_Authorized_Returns201() throws Exception {
        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v2/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeId").value("EMP-1001"))
                .andExpect(jsonPath("$.data.fullName").value("Sarah Connor"));
    }


    @Test
    @WithMockUser(roles = {"EMPLOYEE"})
    @DisplayName("POST /api/v2/employees - Forbidden for regular employee (HTTP 403 Forbidden)")
    void createEmployee_UnauthorizedRole_Returns403() throws Exception {
        mockMvc.perform(post("/api/v2/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /api/v2/employees/{id} - Get employee by document ID (HTTP 200 OK)")
    void getEmployeeById_Returns200() throws Exception {
        when(employeeService.getEmployeeById("emp-doc-1001")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v2/employees/emp-doc-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.officialEmail").value("sarah.connor@techknife.com"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    @DisplayName("GET /api/v2/employees - List employees with pagination (HTTP 200 OK)")
    void getAllEmployees_ReturnsPagedResponse() throws Exception {
        PagedResponse<EmployeeResponse> pagedResponse = PagedResponse.<EmployeeResponse>builder()
                .content(List.of(sampleResponse))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(employeeService.getAllEmployees(0, 10, null, null, null, null)).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v2/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].employeeId").value("EMP-1001"));
    }

    @Test
    @WithMockUser(roles = {"HR"})
    @DisplayName("PATCH /api/v2/employees/{id}/status - Update status (HTTP 200 OK)")
    void updateEmployeeStatus_Returns200() throws Exception {
        when(employeeService.updateEmployeeStatus(eq("emp-doc-1001"), any(UpdateEmployeeStatusRequest.class)))
                .thenReturn(sampleResponse);

        UpdateEmployeeStatusRequest statusRequest = new UpdateEmployeeStatusRequest();
        statusRequest.setStatus(EmployeeStatus.ACTIVE);
        statusRequest.setStatusReason("Verification complete");

        mockMvc.perform(patch("/api/v2/employees/emp-doc-1001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = {"SUPER_ADMIN"})
    @DisplayName("DELETE /api/v2/employees/{id} - Delete employee (HTTP 200 OK)")
    void deleteEmployee_Returns200() throws Exception {
        doNothing().when(employeeService).deleteEmployee("emp-doc-1001");

        mockMvc.perform(delete("/api/v2/employees/emp-doc-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee record deleted successfully"));
    }
}
