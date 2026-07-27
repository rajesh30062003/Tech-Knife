package com.techknife.employee.service;

import com.techknife.backend.dto.PagedResponse;
import com.techknife.employee.dto.BulkImportResult;
import com.techknife.employee.dto.BulkTransferDepartmentRequest;
import com.techknife.employee.dto.BulkUpdateStatusRequest;
import com.techknife.employee.dto.CreateEmployeeRequest;
import com.techknife.employee.dto.EmployeeDTO;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.dto.EmployeeSearchFilter;
import com.techknife.employee.dto.EmployeeSummaryResponse;
import com.techknife.employee.dto.OrgTreeNode;
import com.techknife.employee.dto.ReportingHierarchyResponse;
import com.techknife.employee.dto.UpdateEmployeeRequest;
import com.techknife.employee.dto.UpdateEmployeeStatusRequest;
import com.techknife.employee.entity.DocumentType;
import com.techknife.employee.entity.Education;
import com.techknife.employee.entity.EmployeeDocument;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.entity.EmployeeTimelineRecord;
import com.techknife.employee.entity.Experience;
import com.techknife.employee.entity.Skill;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface defining business operations for Employee management in the Tech Knife platform.
 */
public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request);

    EmployeeResponse getEmployeeById(String id);

    EmployeeResponse getEmployeeByEmployeeId(String employeeId);

    EmployeeResponse getEmployeeByOfficialEmail(String officialEmail);

    PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String search, String departmentId, String managerId, String status);

    PagedResponse<EmployeeSummaryResponse> searchEmployees(EmployeeSearchFilter filter);

    List<EmployeeResponse> getEmployeesByDepartment(String departmentId);

    List<EmployeeResponse> getDirectReports(String managerId);

    void deleteEmployee(String id);

    EmployeeResponse updateEmployeeStatus(String id, UpdateEmployeeStatusRequest request);

    EmployeeResponse updateEmployeeStatus(String id, EmployeeStatus status);

    EmployeeDTO getEmployeeDtoById(String id);

    // Profile Status Transitions
    EmployeeResponse changeEmployeeStatus(String id, EmployeeStatus newStatus, String reason);
    EmployeeResponse activateEmployee(String id, String reason);
    EmployeeResponse deactivateEmployee(String id, String reason);
    EmployeeResponse suspendEmployee(String id, String reason);
    EmployeeResponse terminateEmployee(String id, String reason);
    EmployeeResponse resignEmployee(String id, String reason);
    EmployeeResponse retireEmployee(String id, String reason);

    // Timeline & History
    List<EmployeeTimelineRecord> getEmployeeTimeline(String employeeId);

    // Reporting Hierarchy
    ReportingHierarchyResponse getReportingHierarchy(String employeeId);
    OrgTreeNode getOrgTree(String rootEmployeeId);

    // Bulk Operations
    BulkImportResult bulkImportEmployees(MultipartFile file);
    byte[] exportEmployees(String format);
    List<EmployeeResponse> bulkUpdateStatus(BulkUpdateStatusRequest request);
    List<EmployeeResponse> bulkTransferDepartment(BulkTransferDepartmentRequest request);

    // Sub-resource management
    EmployeeDocument uploadEmployeeDocument(String employeeId, MultipartFile file, DocumentType documentType);

    List<EmployeeDocument> getEmployeeDocuments(String employeeId);

    void deleteEmployeeDocument(String employeeId, String documentId);

    EmployeeResponse addSkill(String employeeId, Skill skill);

    EmployeeResponse updateSkill(String employeeId, String skillId, Skill skill);

    EmployeeResponse deleteSkill(String employeeId, String skillId);

    EmployeeResponse addEducation(String employeeId, Education education);

    EmployeeResponse updateEducation(String employeeId, String educationId, Education education);

    EmployeeResponse deleteEducation(String employeeId, String educationId);

    EmployeeResponse addExperience(String employeeId, Experience experience);

    EmployeeResponse updateExperience(String employeeId, String experienceId, Experience experience);

    EmployeeResponse deleteExperience(String employeeId, String experienceId);
}
