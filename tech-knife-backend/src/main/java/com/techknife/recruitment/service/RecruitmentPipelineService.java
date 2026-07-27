package com.techknife.recruitment.service;

import com.techknife.recruitment.dto.ApplicationDTO;
import com.techknife.recruitment.dto.RecruitmentPipelineDTO;
import com.techknife.recruitment.entity.Application;
import com.techknife.recruitment.entity.JobPosting;
import com.techknife.recruitment.repository.ApplicationRepository;
import com.techknife.recruitment.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentPipelineService {

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;

    public RecruitmentPipelineDTO getPipelineForJobPosting(String jobPostingId) {
        JobPosting job = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + jobPostingId));

        List<Application> applications = applicationRepository.findByJobPostingId(jobPostingId);
        List<ApplicationDTO> applicationDTOs = applications.stream()
                .map(applicationService::mapToDTO)
                .collect(Collectors.toList());

        Map<String, Long> stageCounts = new HashMap<>();
        Map<String, List<ApplicationDTO>> applicationsByStage = new HashMap<>();

        String[] stages = {"APPLIED", "SHORTLISTED", "INTERVIEW_SCHEDULED", "HOLD", "REJECTED", "OFFER_SENT", "OFFER_ACCEPTED", "OFFER_DECLINED", "JOINED"};

        for (String stage : stages) {
            List<ApplicationDTO> filtered = applicationDTOs.stream()
                    .filter(a -> stage.equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
            stageCounts.put(stage, (long) filtered.size());
            applicationsByStage.put(stage, filtered);
        }

        return RecruitmentPipelineDTO.builder()
                .jobPostingId(job.getId())
                .jobTitle(job.getTitle())
                .department(job.getDepartment())
                .totalApplications((long) applications.size())
                .stageCounts(stageCounts)
                .applicationsByStage(applicationsByStage)
                .build();
    }

    public Map<String, Object> getOverallRecruitmentMetrics() {
        long totalJobs = jobPostingRepository.count();
        long totalApplications = applicationRepository.count();

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalJobPostings", totalJobs);
        metrics.put("totalApplications", totalApplications);

        String[] stages = {"APPLIED", "SHORTLISTED", "INTERVIEW_SCHEDULED", "HOLD", "REJECTED", "OFFER_SENT", "OFFER_ACCEPTED", "OFFER_DECLINED", "JOINED"};
        Map<String, Long> overallStageCounts = new HashMap<>();

        for (String stage : stages) {
            long count = applicationRepository.findByStatus(stage).size();
            overallStageCounts.put(stage, count);
        }

        metrics.put("stageBreakdown", overallStageCounts);
        return metrics;
    }
}
