package com.techknife.recruitment.service;

import com.techknife.recruitment.dto.JobPostingDTO;
import com.techknife.recruitment.entity.JobPosting;
import com.techknife.recruitment.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public List<JobPostingDTO> getAllJobPostings(String status, String department) {
        List<JobPosting> jobs;
        if (status != null && department != null) {
            jobs = jobPostingRepository.findByDepartmentAndStatus(department, status);
        } else if (status != null) {
            jobs = jobPostingRepository.findByStatus(status);
        } else if (department != null) {
            jobs = jobPostingRepository.findByDepartment(department);
        } else {
            jobs = jobPostingRepository.findAll();
        }
        return jobs.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public JobPostingDTO getJobPostingById(String id) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));
        return mapToDTO(job);
    }

    public JobPostingDTO createJobPosting(JobPostingDTO dto) {
        String jobCode = "JOB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        JobPosting job = JobPosting.builder()
                .jobCode(jobCode)
                .title(dto.getTitle())
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .employmentType(dto.getEmploymentType() != null ? dto.getEmploymentType() : "FULL_TIME")
                .experience(dto.getExperience())
                .minSalary(dto.getMinSalary())
                .maxSalary(dto.getMaxSalary())
                .salaryRange(dto.getSalaryRange() != null ? dto.getSalaryRange() : (dto.getMinSalary() + " - " + dto.getMaxSalary()))
                .location(dto.getLocation())
                .skillsRequired(dto.getSkillsRequired())
                .description(dto.getDescription())
                .responsibilities(dto.getResponsibilities())
                .qualifications(dto.getQualifications())
                .status(dto.getStatus() != null ? dto.getStatus() : "PUBLISHED")
                .applicationDeadline(dto.getApplicationDeadline())
                .build();

        JobPosting saved = jobPostingRepository.save(job);
        log.info("Created job posting: {} - {}", saved.getJobCode(), saved.getTitle());
        return mapToDTO(saved);
    }

    public JobPostingDTO updateJobPosting(String id, JobPostingDTO dto) {
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));

        if (dto.getTitle() != null) job.setTitle(dto.getTitle());
        if (dto.getDepartment() != null) job.setDepartment(dto.getDepartment());
        if (dto.getDesignation() != null) job.setDesignation(dto.getDesignation());
        if (dto.getEmploymentType() != null) job.setEmploymentType(dto.getEmploymentType());
        if (dto.getExperience() != null) job.setExperience(dto.getExperience());
        if (dto.getMinSalary() != null) job.setMinSalary(dto.getMinSalary());
        if (dto.getMaxSalary() != null) job.setMaxSalary(dto.getMaxSalary());
        if (dto.getSalaryRange() != null) job.setSalaryRange(dto.getSalaryRange());
        if (dto.getLocation() != null) job.setLocation(dto.getLocation());
        if (dto.getSkillsRequired() != null) job.setSkillsRequired(dto.getSkillsRequired());
        if (dto.getDescription() != null) job.setDescription(dto.getDescription());
        if (dto.getResponsibilities() != null) job.setResponsibilities(dto.getResponsibilities());
        if (dto.getQualifications() != null) job.setQualifications(dto.getQualifications());
        if (dto.getStatus() != null) job.setStatus(dto.getStatus());
        if (dto.getApplicationDeadline() != null) job.setApplicationDeadline(dto.getApplicationDeadline());

        JobPosting updated = jobPostingRepository.save(job);
        return mapToDTO(updated);
    }

    public void deleteJobPosting(String id) {
        if (!jobPostingRepository.existsById(id)) {
            throw new RuntimeException("Job posting not found with id: " + id);
        }
        jobPostingRepository.deleteById(id);
    }

    private JobPostingDTO mapToDTO(JobPosting job) {
        return JobPostingDTO.builder()
                .id(job.getId())
                .jobCode(job.getJobCode())
                .title(job.getTitle())
                .department(job.getDepartment())
                .designation(job.getDesignation())
                .employmentType(job.getEmploymentType())
                .experience(job.getExperience())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .salaryRange(job.getSalaryRange())
                .location(job.getLocation())
                .skillsRequired(job.getSkillsRequired())
                .description(job.getDescription())
                .responsibilities(job.getResponsibilities())
                .qualifications(job.getQualifications())
                .status(job.getStatus())
                .applicationDeadline(job.getApplicationDeadline())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
