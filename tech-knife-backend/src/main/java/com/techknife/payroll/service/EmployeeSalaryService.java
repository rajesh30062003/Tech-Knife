package com.techknife.payroll.service;

import com.techknife.payroll.dto.EmployeeSalaryDTO;

import java.util.List;

public interface EmployeeSalaryService {
    List<EmployeeSalaryDTO> getAllEmployeeSalaries();
    List<EmployeeSalaryDTO> getSalariesByEmployeeId(String employeeId);
    EmployeeSalaryDTO getEmployeeSalaryById(String id);
    EmployeeSalaryDTO createEmployeeSalary(EmployeeSalaryDTO dto);
    EmployeeSalaryDTO updateEmployeeSalary(String id, EmployeeSalaryDTO dto);
    void deleteEmployeeSalary(String id);
}
