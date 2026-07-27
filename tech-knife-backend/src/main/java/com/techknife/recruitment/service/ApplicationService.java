package com.techknife.recruitment.service;

import com.techknife.recruitment.dto.ApplicationDTO;
import com.techknife.recruitment.entity.Application;
import com.techknife.recruitment.entity.Candidate;
import com.techknife.recruitment.entity.JobPosting;
import com.techknife.recruitment.repository.ApplicationRepository;
import com.techknife.recruitment.repository.CandidateRepository;
import com.techknife.recruitment.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;

    public List<ApplicationDTO> getAllApplications(String jobPostingId, String candidateId, String status) {
        List<Application> list;
        if (jobPostingId != null && status != null) {
            list = applicationRepository.findByJobPostingIdAndStatus(jobPostingId, status);
        } else if (jobPostingId != null) {
            list = applicationRepository.findByJobPostingId(jobPostingId);
        } else if (candidateId != null) {
            list = applicationRepository.findByCandidateId(candidateId);
        } else if (status != null) {
            list = applicationRepository.findByStatus(status);
        } else {
            list = applicationRepository.findAll();
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ApplicationDTO getApplicationById(String id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
        return mapToDTO(application);
    }

    public ApplicationDTO applyForJob(ApplicationDTO dto) {
        Candidate candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + dto.getCandidateId()));

        JobPosting jobPosting = jobPostingRepository.findById(dto.getJobPostingId())
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + dto.getJobPostingId()));

        applicationRepository.findByCandidateIdAndJobPostingId(dto.getCandidateId(), dto.getJobPostingId())
                .ifPresent(app -> {
                    throw new RuntimeException("Candidate has already applied for this job posting");
                });

        Application application = Application.builder()
                .candidateId(candidate.getId())
                .jobPostingId(jobPosting.getId())
                .status("APPLIED")
                .appliedDate(Instant.now())
                .notes(dto.getNotes())
                .build();

        Application saved = applicationRepository.save(application);
        log.info("Candidate {} applied for Job Posting {}", candidate.getFirstName() + " " + candidate.getLastName(), jobPosting.getJobCode());
        return mapToDTO(saved);
    }

    public ApplicationDTO updateApplicationStatus(String id, String status, String notes) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));

        application.setStatus(status);
        if (notes != null) {
            application.setNotes(notes);
        }

        Application updated = applicationRepository.save(application);

        // Update candidate status if applicable
        Candidate candidate = candidateRepository.findById(updated.getCandidateId()).orElse(null);
        if (candidate != null) {
            if ("SHORTLISTED".equalsIgnoreCase(status)) {
                candidate.setStatus("SHORTLISTED");
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                candidate.setStatus("REJECTED");
            } else if ("INTERVIEW_SCHEDULED".equalsIgnoreCase(status)) {
                candidate.setStatus("INTERVIEWING");
            }
            candidateRepository.save(candidate);
        }

        return mapToDTO(updated);
    }

    public void deleteApplication(String id) {
        if (!applicationRepository.existsById(id)) {
            throw new RuntimeException("Application not found with id: " + id);
        }
        applicationRepository.deleteById(id);
    }

    public ApplicationDTO mapToDTO(Application app) {
        Candidate c = candidateRepository.findById(app.getCandidateId()).orElse(null);
        JobPosting j = jobPostingRepository.findById(app.getJobPostingId()).orElse(null);

        return ApplicationDTO.builder()
                .id(app.getId())
                .candidateId(app.getCandidateId())
                .jobPostingId(app.getJobPostingId())
                .candidateName(c != null ? (c.getFirstName() + " " + c.getLastName()) : "Unknown")
                .candidateEmail(c != null ? c.getEmail() : null)
                .jobTitle(j != null ? j.getTitle() : "Unknown")
                .department(j != null ? j.getDepartment() : null)
                .status(app.getStatus())
                .appliedDate(app.getAppliedDate())
                .notes(app.getNotes())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
