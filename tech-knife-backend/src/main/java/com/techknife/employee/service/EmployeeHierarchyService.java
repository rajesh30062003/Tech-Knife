package com.techknife.employee.service;

import com.techknife.employee.dto.OrgTreeNodeResponse;
import com.techknife.employee.dto.ReportingHierarchyResponse;

import java.util.List;

public interface EmployeeHierarchyService {

    ReportingHierarchyResponse getReportingHierarchy(String employeeId);

    List<OrgTreeNodeResponse> getOrganizationTree(String companyId, String departmentId);
}
