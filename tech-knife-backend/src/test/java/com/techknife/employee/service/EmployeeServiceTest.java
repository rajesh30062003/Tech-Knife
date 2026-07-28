package com.techknife.employee.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.dto.CreateEmployeeRequest;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.dto.EmployeeSearchFilter;
import com.techknife.employee.dto.EmployeeSummaryResponse;
import com.techknife.employee.dto.UpdateEmployeeRequest;
import com.techknife.employee.dto.UpdateEmployeeStatusRequest;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private com.techknife.employee.repository.EmployeeTimelineRepository timelineRepository;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EmployeeServiceImpl employeeService;



    private Employee sampleEmployee;
    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;

    @BeforeEach
    void setUp() {
        sampleEmployee = Employee.builder()
                .id("emp-doc-1")
                .employeeId("EMP-1001")
                .officialEmail("sarah.connor@techknife.com")
                .firstName("Sarah")
                .lastName("Connor")
                .departmentId("Engineering & DevOps")
                .designationId("Chief Technology Officer")
                .salary(java.math.BigDecimal.valueOf(210000.0))
                .status(EmployeeStatus.ACTIVE)
                .build();

        createRequest = new CreateEmployeeRequest();
        createRequest.setEmployeeId("EMP-1001");
        createRequest.setOfficialEmail("sarah.connor@techknife.com");
        createRequest.setFirstName("Sarah");
        createRequest.setLastName("Connor");
        createRequest.setPrimaryMobile("+1 (555) 018-9921");
        createRequest.setDepartmentId("Engineering & DevOps");
        createRequest.setDesignationId("Chief Technology Officer");
        createRequest.setJoiningDate(java.time.LocalDate.parse("2021-06-01"));
        createRequest.setSalary(java.math.BigDecimal.valueOf(210000.0));

        updateRequest = new UpdateEmployeeRequest();
        updateRequest.setFirstName("Sarah");
        updateRequest.setLastName("Connor-Tech");
        updateRequest.setSalary(java.math.BigDecimal.valueOf(225000.0));
    }


    @Test
    @DisplayName("Should successfully onboard a new employee")
    void createEmployee_Success() {
        when(employeeRepository.existsByEmployeeId("EMP-1001")).thenReturn(false);
        when(employeeRepository.existsByOfficialEmail("sarah.connor@techknife.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.createEmployee(createRequest);

        assertNotNull(response);
        assertEquals("EMP-1001", response.getEmployeeId());
        assertEquals("sarah.connor@techknife.com", response.getOfficialEmail());
        assertEquals("Sarah Connor", response.getFullName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when Employee ID already exists")
    void createEmployee_DuplicateEmployeeId_ThrowsBadRequest() {
        when(employeeRepository.existsByEmployeeId("EMP-1001")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(createRequest));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when Official Email already exists")
    void createEmployee_DuplicateOfficialEmail_ThrowsBadRequest() {
        when(employeeRepository.existsByEmployeeId("EMP-1001")).thenReturn(false);
        when(employeeRepository.existsByOfficialEmail("sarah.connor@techknife.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> employeeService.createEmployee(createRequest));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should update existing employee profile")
    void updateEmployee_Success() {
        when(employeeRepository.findById("emp-doc-1")).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.updateEmployee("emp-doc-1", updateRequest);

        assertNotNull(response);
        verify(employeeRepository, times(1)).save(sampleEmployee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent employee")
    void updateEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee("invalid-id", updateRequest));
    }

    @Test
    @DisplayName("Should get employee by document ID")
    void getEmployeeById_Success() {
        when(employeeRepository.findById("emp-doc-1")).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse response = employeeService.getEmployeeById("emp-doc-1");

        assertNotNull(response);
        assertEquals("EMP-1001", response.getEmployeeId());
    }

    @Test
    @DisplayName("Should get employee by employee code")
    void getEmployeeByEmployeeId_Success() {
        when(employeeRepository.findByEmployeeId("EMP-1001")).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse response = employeeService.getEmployeeByEmployeeId("EMP-1001");

        assertNotNull(response);
        assertEquals("sarah.connor@techknife.com", response.getOfficialEmail());
    }

    @Test
    @DisplayName("Should retrieve paginated list of employees")
    void getAllEmployees_Success() {
        Page<Employee> page = new PageImpl<>(List.of(sampleEmployee));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        PagedResponse<EmployeeResponse> response = employeeService.getAllEmployees(0, 10, null, null, null, null);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(1, response.getTotalElements());
    }

    @Test
    @DisplayName("Should perform dynamic search query")
    void searchEmployees_Success() {
        Page<Employee> page = new PageImpl<>(List.of(sampleEmployee));
        when(employeeRepository.searchEmployees(any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        EmployeeSearchFilter filter = new EmployeeSearchFilter();
        filter.setSearchTerm("Sarah");
        filter.setPage(0);
        filter.setSize(10);

        PagedResponse<EmployeeSummaryResponse> response = employeeService.searchEmployees(filter);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Sarah Connor", response.getContent().get(0).getFullName());
    }

    @Test
    @DisplayName("Should update employee status")
    void updateEmployeeStatus_Success() {
        when(employeeRepository.findById("emp-doc-1")).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        UpdateEmployeeStatusRequest request = new UpdateEmployeeStatusRequest();
        request.setStatus(EmployeeStatus.INACTIVE);
        request.setStatusReason("Leave of absence");

        EmployeeResponse response = employeeService.updateEmployeeStatus("emp-doc-1", request);

        assertNotNull(response);
        verify(employeeRepository, times(1)).save(sampleEmployee);
    }

    @Test
    @DisplayName("Should delete employee by ID")
    void deleteEmployee_Success() {
        when(employeeRepository.existsById("emp-doc-1")).thenReturn(true);
        doNothing().when(employeeRepository).deleteById("emp-doc-1");

        assertDoesNotThrow(() -> employeeService.deleteEmployee("emp-doc-1"));
        verify(employeeRepository, times(1)).deleteById("emp-doc-1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent employee")
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeRepository.existsById("invalid-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee("invalid-id"));
    }
}
