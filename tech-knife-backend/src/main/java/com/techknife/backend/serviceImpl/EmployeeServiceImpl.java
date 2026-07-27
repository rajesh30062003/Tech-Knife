package com.techknife.backend.serviceImpl;

import com.techknife.backend.dto.CreateEmployeeRequest;
import com.techknife.backend.dto.EmployeeResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.dto.UpdateEmployeeRequest;
import com.techknife.backend.entity.Employee;
import com.techknife.backend.exception.BadRequestException;
import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.backend.mapper.EmployeeMapper;
import com.techknife.backend.repository.EmployeeRepository;
import com.techknife.backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException("Employee ID '" + request.getEmployeeId() + "' already exists");
        }

        if (employeeRepository.existsByOfficialEmail(request.getOfficialEmail())) {
            throw new BadRequestException("Official Email '" + request.getOfficialEmail() + "' already exists");
        }

        Employee employee = employeeMapper.toEntity(request);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employeeMapper.updateEntityFromRequest(request, employee);
        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByOfficialEmail(String officialEmail) {
        Employee employee = employeeRepository.findByOfficialEmail(officialEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "officialEmail", officialEmail));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, String departmentId, String managerId, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Employee> employeePage;

        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.searchByName(search.trim(), pageable);
        } else if (departmentId != null && !departmentId.trim().isEmpty()) {
            employeePage = employeeRepository.findByDepartmentId(departmentId.trim(), pageable);
        } else if (managerId != null && !managerId.trim().isEmpty()) {
            employeePage = employeeRepository.findByManagerId(managerId.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                Employee.EmployeeStatus employeeStatus = Employee.EmployeeStatus.valueOf(status.trim().toUpperCase());
                employeePage = employeeRepository.findByStatus(employeeStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid employee status: " + status);
            }
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        List<EmployeeResponse> content = employeePage.getContent().stream()
                .map(employeeMapper::toResponse)
                .toList();

        return PagedResponse.<EmployeeResponse>builder()
                .content(content)
                .page(employeePage.getNumber())
                .size(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .last(employeePage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteEmployee(String id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployeeStatus(String id, Employee.EmployeeStatus status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setStatus(status);
        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponse(updated);
    }
}
