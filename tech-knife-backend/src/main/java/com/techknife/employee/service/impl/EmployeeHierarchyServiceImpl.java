package com.techknife.employee.service.impl;

import com.techknife.backend.exception.ResourceNotFoundException;
import com.techknife.employee.dto.EmployeeSummaryResponse;
import com.techknife.employee.dto.OrgTreeNodeResponse;
import com.techknife.employee.dto.ReportingHierarchyResponse;
import com.techknife.employee.entity.Employee;
import com.techknife.employee.repository.EmployeeRepository;
import com.techknife.employee.service.EmployeeHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeHierarchyServiceImpl implements EmployeeHierarchyService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportingHierarchyResponse getReportingHierarchy(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        EmployeeSummaryResponse directManagerSummary = null;
        EmployeeSummaryResponse skipLevelManagerSummary = null;

        if (employee.getManagerId() != null && !employee.getManagerId().trim().isEmpty()) {
            Employee directManager = employeeRepository.findById(employee.getManagerId()).orElse(null);
            if (directManager != null) {
                directManagerSummary = mapToSummary(directManager);

                if (directManager.getManagerId() != null && !directManager.getManagerId().trim().isEmpty()) {
                    Employee skipLevelManager = employeeRepository.findById(directManager.getManagerId()).orElse(null);
                    if (skipLevelManager != null) {
                        skipLevelManagerSummary = mapToSummary(skipLevelManager);
                    }
                }
            }
        }

        List<EmployeeSummaryResponse> directReports = employeeRepository.findByManagerId(employeeId).stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        String fullName = (employee.getFirstName() + " " + employee.getLastName()).trim();

        return ReportingHierarchyResponse.builder()
                .employeeId(employee.getId())
                .fullName(fullName)
                .designationId(employee.getDesignationId())
                .departmentId(employee.getDepartmentId())
                .directManager(directManagerSummary)
                .skipLevelManager(skipLevelManagerSummary)
                .directReports(directReports)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgTreeNodeResponse> getOrganizationTree(String companyId, String departmentId) {
        List<Employee> allEmployees;
        if (departmentId != null && !departmentId.trim().isEmpty()) {
            allEmployees = employeeRepository.findByDepartmentId(departmentId);
        } else if (companyId != null && !companyId.trim().isEmpty()) {
            allEmployees = employeeRepository.findByCompanyId(companyId);
        } else {
            allEmployees = employeeRepository.findAll();
        }

        // Find root employees (who have no manager or manager not in list)
        List<Employee> roots = allEmployees.stream()
                .filter(e -> e.getManagerId() == null || e.getManagerId().trim().isEmpty() ||
                             allEmployees.stream().noneMatch(parent -> parent.getId().equals(e.getManagerId())))
                .collect(Collectors.toList());

        return roots.stream()
                .map(root -> buildTreeNode(root, allEmployees))
                .collect(Collectors.toList());
    }

    private OrgTreeNodeResponse buildTreeNode(Employee employee, List<Employee> allEmployees) {
        String fullName = (employee.getFirstName() + " " + employee.getLastName()).trim();

        List<OrgTreeNodeResponse> subordinates = allEmployees.stream()
                .filter(e -> employee.getId().equals(e.getManagerId()))
                .map(sub -> buildTreeNode(sub, allEmployees))
                .collect(Collectors.toList());

        return OrgTreeNodeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .fullName(fullName)
                .designationId(employee.getDesignationId())
                .departmentId(employee.getDepartmentId())
                .status(employee.getStatus())
                .profileImage(employee.getProfileImage())
                .subordinates(subordinates)
                .build();
    }

    private EmployeeSummaryResponse mapToSummary(Employee employee) {
        String fullName = (employee.getFirstName() + " " + employee.getLastName()).trim();
        return EmployeeSummaryResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .fullName(fullName)
                .officialEmail(employee.getOfficialEmail())
                .departmentId(employee.getDepartmentId())
                .designationId(employee.getDesignationId())
                .status(employee.getStatus())
                .profileImage(employee.getProfileImage())
                .build();
    }
}
