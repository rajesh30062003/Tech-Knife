package com.techknife.employee.service;

import com.techknife.employee.dto.EmployeeResponse;

public interface EmployeeProfileService {

    EmployeeResponse activateProfile(String employeeId, String remarks, String updatedBy);

    EmployeeResponse deactivateProfile(String employeeId, String remarks, String updatedBy);

    EmployeeResponse suspendProfile(String employeeId, String remarks, String updatedBy);

    EmployeeResponse terminateProfile(String employeeId, String remarks, String updatedBy);

    EmployeeResponse resignProfile(String employeeId, String remarks, String updatedBy);

    EmployeeResponse retireProfile(String employeeId, String remarks, String updatedBy);
}
