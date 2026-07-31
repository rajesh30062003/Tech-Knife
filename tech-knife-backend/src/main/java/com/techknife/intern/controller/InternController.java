package com.techknife.intern.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.intern.dto.AssignMentorRequest;
import com.techknife.intern.dto.CreateInternEvaluationRequest;
import com.techknife.intern.dto.CreateInternRequest;
import com.techknife.intern.dto.CreateInternTaskRequest;
import com.techknife.intern.dto.GenerateCertificateRequest;
import com.techknife.intern.dto.InternAttendanceSummaryResponse;
import com.techknife.intern.dto.InternCertificateResponse;
import com.techknife.intern.dto.InternEvaluationResponse;
import com.techknife.intern.dto.InternMentorResponse;
import com.techknife.intern.dto.InternResponse;
import com.techknife.intern.dto.InternTaskResponse;
import com.techknife.intern.dto.UpdateAttendanceSummaryRequest;
import com.techknife.intern.dto.UpdateInternRequest;
import com.techknife.intern.dto.UpdateInternTaskRequest;
import com.techknife.intern.entity.InternStatus;
import com.techknife.intern.service.InternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/interns", "/api/interns", "/api/v1/employees/interns", "/api/employees/interns"})
@RequiredArgsConstructor
@Tag(name = "Intern Management", description = "Endpoints for Intern Onboarding, Mentorship, Tasks, Evaluations, and Certificates")
@SecurityRequirement(name = "bearerAuth")
public class InternController {

    private final InternService internService;

    @PostMapping
    @PreAuthorize("hasAuthority('INTERN_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Auditable(action = "CREATE_INTERN", resourceType = "INTERN")
    @Operation(summary = "Onboard new Intern", description = "Creates a new intern record and publishes onboarding notification")
    public ResponseEntity<ApiResponse<InternResponse>> createIntern(@Valid @RequestBody CreateInternRequest request) {
        InternResponse response = internService.createIntern(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Intern created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Auditable(action = "UPDATE_INTERN", resourceType = "INTERN")
    @Operation(summary = "Update Intern Profile", description = "Updates details of an existing intern")
    public ResponseEntity<ApiResponse<InternResponse>> updateIntern(
            @PathVariable String id,
            @Valid @RequestBody UpdateInternRequest request) {
        InternResponse response = internService.updateIntern(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Intern updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE', 'CEO', 'CTO', 'MD', 'CMO', 'PROJECT_MANAGER', 'DEV') or hasAuthority('INTERN_READ')")
    @Operation(summary = "Get Intern by ID", description = "Retrieves intern record details by MongoDB ID")
    public ResponseEntity<ApiResponse<InternResponse>> getInternById(@PathVariable String id) {
        InternResponse response = internService.getInternById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Intern retrieved successfully"));
    }

    @GetMapping("/code/{internCode}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE', 'CEO', 'CTO', 'MD', 'CMO', 'PROJECT_MANAGER', 'DEV') or hasAuthority('INTERN_READ')")
    @Operation(summary = "Get Intern by Code", description = "Retrieves intern details by unique intern code")
    public ResponseEntity<ApiResponse<InternResponse>> getInternByCode(@PathVariable String internCode) {
        InternResponse response = internService.getInternByCode(internCode);
        return ResponseEntity.ok(ApiResponse.success(response, "Intern retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE', 'CEO', 'CTO', 'MD', 'CMO', 'PROJECT_MANAGER', 'DEV') or hasAuthority('INTERN_READ')")
    @Operation(summary = "Get All Interns", description = "Paginated search and list of interns")
    public ResponseEntity<ApiResponse<PagedResponse<InternResponse>>> getAllInterns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mentor) {
        int effectiveSize = (limit != null && limit > 0) ? limit : size;
        PagedResponse<InternResponse> response = internService.getAllInterns(page, effectiveSize, search, departmentId, department, status, mentor);
        return ResponseEntity.ok(ApiResponse.success(response, "Interns retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INTERN_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_INTERN", resourceType = "INTERN")
    @Operation(summary = "Delete Intern", description = "Removes an intern record")
    public ResponseEntity<ApiResponse<Void>> deleteIntern(@PathVariable String id) {
        internService.deleteIntern(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Intern deleted successfully"));
    }

    // --- MENTORSHIP ENDPOINTS ---

    @PostMapping("/{id}/mentor")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ASSIGN_INTERN_MENTOR", resourceType = "INTERN")
    @Operation(summary = "Assign or Change Mentor", description = "Assigns mentor with max intern validation")
    public ResponseEntity<ApiResponse<InternMentorResponse>> assignMentor(
            @PathVariable String id,
            @Valid @RequestBody AssignMentorRequest request) {
        InternMentorResponse response = internService.assignMentor(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Mentor assigned successfully"));
    }

    @GetMapping("/{id}/mentor-history")
    @PreAuthorize("hasAuthority('INTERN_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Mentor Assignment History", description = "Retrieves historic mentor assignments")
    public ResponseEntity<ApiResponse<List<InternMentorResponse>>> getMentorHistory(@PathVariable String id) {
        List<InternMentorResponse> response = internService.getMentorHistory(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Mentor history retrieved successfully"));
    }

    // --- TASK MANAGEMENT ENDPOINTS ---

    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ASSIGN_INTERN_TASK", resourceType = "INTERN_TASK")
    @Operation(summary = "Assign Task to Intern", description = "Assigns a work task with deadline and priority")
    public ResponseEntity<ApiResponse<InternTaskResponse>> assignTask(
            @PathVariable String id,
            @Valid @RequestBody CreateInternTaskRequest request) {
        InternTaskResponse response = internService.assignTask(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Task assigned successfully"));
    }

    @PutMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_INTERN_TASK", resourceType = "INTERN_TASK")
    @Operation(summary = "Update Intern Task", description = "Updates task status, progress percentage, or review remarks")
    public ResponseEntity<ApiResponse<InternTaskResponse>> updateTask(
            @PathVariable String id,
            @PathVariable String taskId,
            @RequestBody UpdateInternTaskRequest request) {
        InternTaskResponse response = internService.updateTask(id, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Task updated successfully"));
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('INTERN_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Intern Tasks", description = "Lists all tasks assigned to an intern")
    public ResponseEntity<ApiResponse<List<InternTaskResponse>>> getInternTasks(@PathVariable String id) {
        List<InternTaskResponse> response = internService.getInternTasks(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Tasks retrieved successfully"));
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_INTERN_TASK", resourceType = "INTERN_TASK")
    @Operation(summary = "Delete Intern Task", description = "Deletes an assigned task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String id, @PathVariable String taskId) {
        internService.deleteTask(id, taskId);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }

    // --- EVALUATION ENDPOINTS ---

    @PostMapping("/{id}/evaluations")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_INTERN_EVALUATION", resourceType = "INTERN_EVALUATION")
    @Operation(summary = "Submit Intern Evaluation", description = "Submits performance score and conversion recommendation")
    public ResponseEntity<ApiResponse<InternEvaluationResponse>> createEvaluation(
            @PathVariable String id,
            @Valid @RequestBody CreateInternEvaluationRequest request) {
        InternEvaluationResponse response = internService.createEvaluation(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Evaluation submitted successfully"));
    }

    @GetMapping("/{id}/evaluations")
    @PreAuthorize("hasAuthority('INTERN_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Intern Evaluations", description = "Retrieves all performance evaluation records")
    public ResponseEntity<ApiResponse<List<InternEvaluationResponse>>> getInternEvaluations(@PathVariable String id) {
        List<InternEvaluationResponse> response = internService.getInternEvaluations(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Evaluations retrieved successfully"));
    }

    // --- ATTENDANCE SUMMARY ENDPOINTS ---

    @PostMapping("/{id}/attendance-summary")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_INTERN_ATTENDANCE", resourceType = "INTERN_ATTENDANCE")
    @Operation(summary = "Update Monthly Attendance Summary", description = "Records monthly working and present days")
    public ResponseEntity<ApiResponse<InternAttendanceSummaryResponse>> updateAttendanceSummary(
            @PathVariable String id,
            @Valid @RequestBody UpdateAttendanceSummaryRequest request) {
        InternAttendanceSummaryResponse response = internService.updateAttendanceSummary(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance summary updated successfully"));
    }

    @GetMapping("/{id}/attendance-summary")
    @PreAuthorize("hasAuthority('INTERN_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Monthly Attendance Summaries", description = "Retrieves attendance summary history")
    public ResponseEntity<ApiResponse<List<InternAttendanceSummaryResponse>>> getAttendanceSummaries(@PathVariable String id) {
        List<InternAttendanceSummaryResponse> response = internService.getAttendanceSummaries(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance summaries retrieved successfully"));
    }

    // --- CERTIFICATE ENDPOINTS ---

    @PostMapping("/{id}/generate-certificate")
    @PreAuthorize("hasAuthority('INTERN_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "GENERATE_INTERN_CERTIFICATE", resourceType = "INTERN_CERTIFICATE")
    @Operation(summary = "Generate Internship Certificate", description = "Issues certificate with unique number and verification code")
    public ResponseEntity<ApiResponse<InternCertificateResponse>> generateCertificate(
            @PathVariable String id,
            @RequestBody GenerateCertificateRequest request) {
        InternCertificateResponse response = internService.generateCertificate(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Certificate generated successfully"));
    }

    @GetMapping("/{id}/certificate")
    @PreAuthorize("hasAuthority('INTERN_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Issued Certificate", description = "Retrieves certificate details and download URL")
    public ResponseEntity<ApiResponse<InternCertificateResponse>> getCertificate(@PathVariable String id) {
        InternCertificateResponse response = internService.getCertificate(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Certificate retrieved successfully"));
    }

    // --- CONVERT TO EMPLOYEE ENDPOINT ---

    @PostMapping("/{id}/convert-to-employee")
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CONVERT_INTERN_TO_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Convert Intern to Full-Time Employee", description = "Transforms intern record into an active employee record")
    public ResponseEntity<ApiResponse<EmployeeResponse>> convertToEmployee(@PathVariable String id) {
        EmployeeResponse response = internService.convertToEmployee(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Intern successfully converted to employee"));
    }
}
