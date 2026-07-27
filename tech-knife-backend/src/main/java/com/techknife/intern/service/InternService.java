package com.techknife.intern.service;

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

import java.util.List;

/**
 * Service interface for complete Internship Management operations in Tech Knife Enterprise Platform.
 */
public interface InternService {

    InternResponse createIntern(CreateInternRequest request);

    InternResponse updateIntern(String id, UpdateInternRequest request);

    InternResponse getInternById(String id);

    InternResponse getInternByCode(String internCode);

    InternResponse getInternByOfficialEmail(String officialEmail);

    PagedResponse<InternResponse> getAllInterns(int page, int size, String search, InternStatus status);

    List<InternResponse> getInternsByDepartment(String departmentId);

    List<InternResponse> getInternsByMentor(String mentorId);

    void deleteIntern(String id);

    // Mentor Management
    InternMentorResponse assignMentor(String internId, AssignMentorRequest request);

    List<InternMentorResponse> getMentorHistory(String internId);

    // Task Management
    InternTaskResponse assignTask(String internId, CreateInternTaskRequest request);

    InternTaskResponse updateTask(String internId, String taskId, UpdateInternTaskRequest request);

    List<InternTaskResponse> getInternTasks(String internId);

    void deleteTask(String internId, String taskId);

    // Evaluations
    InternEvaluationResponse createEvaluation(String internId, CreateInternEvaluationRequest request);

    List<InternEvaluationResponse> getInternEvaluations(String internId);

    // Attendance Summary
    InternAttendanceSummaryResponse updateAttendanceSummary(String internId, UpdateAttendanceSummaryRequest request);

    List<InternAttendanceSummaryResponse> getAttendanceSummaries(String internId);

    // Certificate Generation
    InternCertificateResponse generateCertificate(String internId, GenerateCertificateRequest request);

    InternCertificateResponse getCertificate(String internId);

    // Convert to Employee
    EmployeeResponse convertToEmployee(String internId);
}
