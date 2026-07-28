package com.techknife.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportingHierarchyResponse {
    private String id;
    private String employeeId;

    private String fullName;
    private String designationId;
    private String departmentId;

    private EmployeeSummaryResponse directManager;
    private EmployeeSummaryResponse skipLevelManager;
    private List<EmployeeSummaryResponse> directReports;
}
