package com.techknife.recruitment.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.InterviewDTO;
import com.techknife.recruitment.dto.InterviewFeedbackDTO;
import com.techknife.recruitment.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recruitment/interviews")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Interviews", description = "Endpoints for scheduling and managing candidate interviews & feedback")
@SecurityRequirement(name = "bearerAuth")
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping
    @PreAuthorize("hasAuthority('INTERVIEW_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all scheduled interviews")
    public ResponseEntity<ApiResponse<List<InterviewDTO>>> getAllInterviews(
            @RequestParam(required = false) String applicationId,
            @RequestParam(required = false) String candidateId,
            @RequestParam(required = false) String jobPostingId,
            @RequestParam(required = false) String result) {
        List<InterviewDTO> list = interviewService.getAllInterviews(applicationId, candidateId, jobPostingId, result);
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched interviews successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INTERVIEW_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get interview details by ID")
    public ResponseEntity<ApiResponse<InterviewDTO>> getInterviewById(@PathVariable String id) {
        InterviewDTO dto = interviewService.getInterviewById(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Fetched interview details successfully"));
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('INTERVIEW_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "Interview", description = "Scheduled Candidate Interview")
    @Operation(summary = "Schedule a candidate interview")
    public ResponseEntity<ApiResponse<InterviewDTO>> scheduleInterview(@Valid @RequestBody InterviewDTO dto) {
        InterviewDTO result = interviewService.scheduleInterview(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Scheduled interview successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INTERVIEW_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.RECRUITMENT, entityType = "Interview", description = "Updated Interview Details")
    @Operation(summary = "Update interview details")
    public ResponseEntity<ApiResponse<InterviewDTO>> updateInterview(
            @PathVariable String id,
            @Valid @RequestBody InterviewDTO dto) {
        InterviewDTO result = interviewService.updateInterview(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Interview updated successfully"));
    }

    @PostMapping("/feedback")
    @PreAuthorize("hasAuthority('INTERVIEW_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.RECRUITMENT, entityType = "InterviewFeedback", description = "Submitted Interview Feedback")
    @Operation(summary = "Submit interview feedback and evaluation ratings")
    public ResponseEntity<ApiResponse<InterviewFeedbackDTO>> submitFeedback(@Valid @RequestBody InterviewFeedbackDTO dto) {
        InterviewFeedbackDTO result = interviewService.submitFeedback(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Interview feedback submitted successfully"));
    }

    @GetMapping("/{id}/feedback")
    @PreAuthorize("hasAuthority('INTERVIEW_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get feedback entries for an interview")
    public ResponseEntity<ApiResponse<List<InterviewFeedbackDTO>>> getFeedbacksByInterview(@PathVariable String id) {
        List<InterviewFeedbackDTO> list = interviewService.getFeedbacksByInterview(id);
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched interview feedback successfully"));
    }

}
