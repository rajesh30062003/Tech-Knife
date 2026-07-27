package com.techknife.recruitment.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.recruitment.dto.RecruitmentPipelineDTO;
import com.techknife.recruitment.service.RecruitmentPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/recruitment/pipeline")
@RequiredArgsConstructor
@Tag(name = "Recruitment - Pipeline & Analytics", description = "Endpoints for recruitment funnel breakdown and analytics")
@SecurityRequirement(name = "bearerAuth")
public class RecruitmentPipelineController {

    private final RecruitmentPipelineService pipelineService;

    @GetMapping("/job/{jobPostingId}")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get candidate pipeline breakdown for a specific job posting")
    public ResponseEntity<ApiResponse<RecruitmentPipelineDTO>> getPipelineForJobPosting(@PathVariable String jobPostingId) {
        RecruitmentPipelineDTO dto = pipelineService.getPipelineForJobPosting(jobPostingId);
        return ResponseEntity.ok(ApiResponse.success("Fetched job recruitment pipeline successfully", dto));
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasAuthority('CANDIDATE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get overall recruitment metrics and stage breakdown")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallRecruitmentMetrics() {
        Map<String, Object> metrics = pipelineService.getOverallRecruitmentMetrics();
        return ResponseEntity.ok(ApiResponse.success("Fetched overall recruitment metrics successfully", metrics));
    }
}
