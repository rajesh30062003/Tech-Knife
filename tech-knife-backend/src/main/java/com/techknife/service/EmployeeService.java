package com.techknife.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.dto.CreateEmployeeRequest;
import com.techknife.dto.EmployeeResponse;
import com.techknife.dto.UpdateEmployeeRequest;
import com.techknife.entity.Employee;

public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request);

    EmployeeResponse getEmployeeById(String id);

    EmployeeResponse getEmployeeByEmployeeId(String employeeId);

    EmployeeResponse getEmployeeByOfficialEmail(String officialEmail);

    PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, String departmentId, String managerId, String status);

    void deleteEmployee(String id);

    EmployeeResponse updateEmployeeStatus(String id, Employee.EmployeeStatus status);
}
