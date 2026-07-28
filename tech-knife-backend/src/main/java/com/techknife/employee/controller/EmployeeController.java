package com.techknife.employee.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.employee.dto.CreateEmployeeRequest;
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
import com.techknife.employee.entity.EmployeeTimelineRecord;
import com.techknife.employee.entity.Experience;
import com.techknife.employee.entity.Skill;
import com.techknife.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for Employee Management module in Tech Knife Enterprise Platform.
 */
@RestController("employeeFeatureController")
@RequestMapping("/api/v2/employees")
@Auditable(module = "Employee Directory V2")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employee Management V2", description = "Enterprise Staff Directory, Onboarding, Search, Hierarchy, and Status Operations")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(@Qualifier("employeeFeatureServiceImpl") EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Onboard new employee", description = "Creates a new employee record.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Employee onboarded successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Update employee record", description = "Updates fields for an existing employee record.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @Parameter(description = "Employee Document ID") @PathVariable("id") String id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee details updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Get employee profile by ID", description = "Retrieves complete profile details by ID.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @Parameter(description = "Employee Document ID") @PathVariable("id") String id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully"));
    }

    @GetMapping("/code/{employeeId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Get employee by Employee Code", description = "Retrieves profile details by unique employee code.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(
            @Parameter(description = "Employee Code ID") @PathVariable("employeeId") String employeeId) {
        EmployeeResponse response = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully"));
    }

    @GetMapping("/email/{officialEmail}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Get employee by official email", description = "Retrieves profile details by official corporate email.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByOfficialEmail(
            @Parameter(description = "Official Corporate Email") @PathVariable("officialEmail") String officialEmail) {
        EmployeeResponse response = employeeService.getEmployeeByOfficialEmail(officialEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "List employees with basic filters", description = "Fetch paginated employee records.")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "managerId", required = false) String managerId,
            @RequestParam(value = "status", required = false) String status) {
        PagedResponse<EmployeeResponse> response = employeeService.getAllEmployees(page, size, search, departmentId, managerId, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Employees list retrieved successfully"));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Advanced multi-criteria employee search", description = "Dynamically filter and page employees.")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeSummaryResponse>>> searchEmployees(
            @RequestBody EmployeeSearchFilter filter) {
        PagedResponse<EmployeeSummaryResponse> response = employeeService.searchEmployees(filter);
        return ResponseEntity.ok(ApiResponse.success(response, "Search results retrieved successfully"));
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR')")
    @Operation(summary = "Get employees by department", description = "Lists all staff assigned to a department.")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(
            @Parameter(description = "Department Identifier") @PathVariable("departmentId") String departmentId) {
        List<EmployeeResponse> response = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Department employees retrieved successfully"));
    }

    @GetMapping("/manager/{managerId}/reports")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR')")
    @Operation(summary = "Get direct reports for a manager", description = "Lists all employees reporting to manager.")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getDirectReports(
            @Parameter(description = "Manager Employee ID") @PathVariable("managerId") String managerId) {
        List<EmployeeResponse> response = employeeService.getDirectReports(managerId);
        return ResponseEntity.ok(ApiResponse.success(response, "Direct reports retrieved successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Update employee status", description = "Transitions an employee's status.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployeeStatus(
            @Parameter(description = "Employee Document ID") @PathVariable("id") String id,
            @Valid @RequestBody UpdateEmployeeStatusRequest request) {
        EmployeeResponse response = employeeService.updateEmployeeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee status updated successfully"));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Activate employee", description = "Sets employee status to ACTIVE.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> activateEmployee(@PathVariable("id") String id, @RequestParam(required = false) String reason) {
        EmployeeResponse response = employeeService.activateEmployee(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee activated successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Deactivate employee", description = "Sets employee status to INACTIVE.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> deactivateEmployee(@PathVariable("id") String id, @RequestParam(required = false) String reason) {
        EmployeeResponse response = employeeService.deactivateEmployee(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee deactivated successfully"));
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Suspend employee", description = "Sets employee status to SUSPENDED.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> suspendEmployee(@PathVariable("id") String id, @RequestParam(required = false) String reason) {
        EmployeeResponse response = employeeService.suspendEmployee(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee suspended successfully"));
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Terminate employee", description = "Sets employee status to TERMINATED.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> terminateEmployee(@PathVariable("id") String id, @RequestParam(required = false) String reason) {
        EmployeeResponse response = employeeService.terminateEmployee(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee terminated successfully"));
    }

    @PostMapping("/{id}/resign")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Record employee resignation", description = "Sets employee status to RESIGNED.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> resignEmployee(@PathVariable("id") String id, @RequestParam(required = false) String reason) {
        EmployeeResponse response = employeeService.resignEmployee(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee resignation recorded successfully"));
    }

    @PostMapping("/{id}/retire")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Record employee retirement", description = "Sets employee status to RETIRED.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> retireEmployee(@PathVariable("id") String id, @RequestParam(required = false) String reason) {
        EmployeeResponse response = employeeService.retireEmployee(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee retirement recorded successfully"));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Get employee career timeline", description = "Retrieves historical timeline changes for an employee.")
    public ResponseEntity<ApiResponse<List<EmployeeTimelineRecord>>> getEmployeeTimeline(@PathVariable("id") String id) {
        List<EmployeeTimelineRecord> timeline = employeeService.getEmployeeTimeline(id);
        return ResponseEntity.ok(ApiResponse.success(timeline, "Employee timeline retrieved successfully"));
    }

    @GetMapping("/{id}/reporting-hierarchy")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Get reporting hierarchy", description = "Retrieves direct manager, skip level manager, and direct reports.")
    public ResponseEntity<ApiResponse<ReportingHierarchyResponse>> getReportingHierarchy(@PathVariable("id") String id) {
        ReportingHierarchyResponse hierarchy = employeeService.getReportingHierarchy(id);
        return ResponseEntity.ok(ApiResponse.success(hierarchy, "Reporting hierarchy retrieved successfully"));
    }

    @GetMapping("/{id}/org-tree")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Get organizational tree node", description = "Builds full nested subordinate org tree structure starting from specified employee.")
    public ResponseEntity<ApiResponse<OrgTreeNode>> getOrgTree(@PathVariable("id") String id) {
        OrgTreeNode tree = employeeService.getOrgTree(id);
        return ResponseEntity.ok(ApiResponse.success(tree, "Org tree retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete employee record", description = "Deletes an employee document.")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @Parameter(description = "Employee Document ID") @PathVariable("id") String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee record deleted successfully"));
    }

    // --- Sub-resource Endpoints ---

    // Document Endpoints
    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('EMPLOYEE_DOCUMENT_UPLOAD') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Upload employee document", description = "Uploads a document for an employee via Cloudinary storage.")
    public ResponseEntity<ApiResponse<EmployeeDocument>> uploadDocument(
            @PathVariable("id") String employeeId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentType", required = false) DocumentType documentType) {
        EmployeeDocument doc = employeeService.uploadEmployeeDocument(employeeId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(doc, "Document uploaded successfully"));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get employee documents", description = "Lists all documents uploaded for an employee.")
    public ResponseEntity<ApiResponse<List<EmployeeDocument>>> getDocuments(@PathVariable("id") String employeeId) {
        List<EmployeeDocument> docs = employeeService.getEmployeeDocuments(employeeId);
        return ResponseEntity.ok(ApiResponse.success(docs, "Documents retrieved successfully"));
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_DOCUMENT_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Delete employee document", description = "Deletes a document from employee record and Cloudinary.")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable("id") String employeeId,
            @PathVariable("documentId") String documentId) {
        employeeService.deleteEmployeeDocument(employeeId, documentId);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully"));
    }


    // Skill Endpoints
    @PostMapping("/{id}/skills")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Add employee skill", description = "Adds a skill entry to employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> addSkill(
            @PathVariable("id") String employeeId,
            @RequestBody Skill skill) {
        EmployeeResponse response = employeeService.addSkill(employeeId, skill);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Skill added successfully"));
    }

    @PutMapping("/{id}/skills/{skillId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Update employee skill", description = "Updates a skill entry in employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateSkill(
            @PathVariable("id") String employeeId,
            @PathVariable("skillId") String skillId,
            @RequestBody Skill skill) {
        EmployeeResponse response = employeeService.updateSkill(employeeId, skillId, skill);
        return ResponseEntity.ok(ApiResponse.success(response, "Skill updated successfully"));
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Delete employee skill", description = "Removes a skill from employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> deleteSkill(
            @PathVariable("id") String employeeId,
            @PathVariable("skillId") String skillId) {
        EmployeeResponse response = employeeService.deleteSkill(employeeId, skillId);
        return ResponseEntity.ok(ApiResponse.success(response, "Skill deleted successfully"));
    }

    // Education Endpoints
    @PostMapping("/{id}/education")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Add education detail", description = "Adds an education record to employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> addEducation(
            @PathVariable("id") String employeeId,
            @RequestBody Education education) {
        EmployeeResponse response = employeeService.addEducation(employeeId, education);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Education added successfully"));
    }

    @PutMapping("/{id}/education/{educationId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Update education detail", description = "Updates an education record in employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEducation(
            @PathVariable("id") String employeeId,
            @PathVariable("educationId") String educationId,
            @RequestBody Education education) {
        EmployeeResponse response = employeeService.updateEducation(employeeId, educationId, education);
        return ResponseEntity.ok(ApiResponse.success(response, "Education updated successfully"));
    }

    @DeleteMapping("/{id}/education/{educationId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Delete education detail", description = "Removes an education record from employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> deleteEducation(
            @PathVariable("id") String employeeId,
            @PathVariable("educationId") String educationId) {
        EmployeeResponse response = employeeService.deleteEducation(employeeId, educationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Education deleted successfully"));
    }

    // Experience Endpoints
    @PostMapping("/{id}/experience")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Add work experience detail", description = "Adds a prior work experience record to employee profile.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> addExperience(
            @PathVariable("id") String employeeId,
            @RequestBody Experience experience) {
        EmployeeResponse response = employeeService.addExperience(employeeId, experience);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Experience added successfully"));
    }

    @PutMapping("/{id}/experience/{experienceId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Update work experience detail", description = "Updates a work experience record.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateExperience(
            @PathVariable("id") String employeeId,
            @PathVariable("experienceId") String experienceId,
            @RequestBody Experience experience) {
        EmployeeResponse response = employeeService.updateExperience(employeeId, experienceId, experience);
        return ResponseEntity.ok(ApiResponse.success(response, "Experience updated successfully"));
    }

    @DeleteMapping("/{id}/experience/{experienceId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Delete work experience detail", description = "Removes a work experience record.")
    public ResponseEntity<ApiResponse<EmployeeResponse>> deleteExperience(
            @PathVariable("id") String employeeId,
            @PathVariable("experienceId") String experienceId) {
        EmployeeResponse response = employeeService.deleteExperience(employeeId, experienceId);
        return ResponseEntity.ok(ApiResponse.success(response, "Experience deleted successfully"));
    }
}
