package com.techknife.recruitment.service;

import com.techknife.recruitment.dto.InterviewDTO;
import com.techknife.recruitment.dto.InterviewFeedbackDTO;
import com.techknife.recruitment.entity.Application;
import com.techknife.recruitment.entity.Candidate;
import com.techknife.recruitment.entity.Interview;
import com.techknife.recruitment.entity.InterviewFeedback;
import com.techknife.recruitment.entity.JobPosting;
import com.techknife.recruitment.repository.ApplicationRepository;
import com.techknife.recruitment.repository.CandidateRepository;
import com.techknife.recruitment.repository.InterviewFeedbackRepository;
import com.techknife.recruitment.repository.InterviewRepository;
import com.techknife.recruitment.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewFeedbackRepository feedbackRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;

    public List<InterviewDTO> getAllInterviews(String applicationId, String candidateId, String jobPostingId, String result) {
        List<Interview> interviews;
        if (applicationId != null) {
            interviews = interviewRepository.findByApplicationId(applicationId);
        } else if (candidateId != null) {
            interviews = interviewRepository.findByCandidateId(candidateId);
        } else if (jobPostingId != null) {
            interviews = interviewRepository.findByJobPostingId(jobPostingId);
        } else if (result != null) {
            interviews = interviewRepository.findByResult(result);
        } else {
            interviews = interviewRepository.findAll();
        }

        return interviews.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public InterviewDTO getInterviewById(String id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + id));
        return mapToDTO(interview);
    }

    public InterviewDTO scheduleInterview(InterviewDTO dto) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + dto.getApplicationId()));

        Interview interview = Interview.builder()
                .applicationId(application.getId())
                .candidateId(application.getCandidateId())
                .jobPostingId(application.getJobPostingId())
                .interviewType(dto.getInterviewType() != null ? dto.getInterviewType() : "TECHNICAL")
                .mode(dto.getMode() != null ? dto.getMode() : "ONLINE")
                .panelMembers(dto.getPanelMembers())
                .interviewTime(dto.getInterviewTime())
                .locationOrLink(dto.getLocationOrLink())
                .result("SCHEDULED")
                .build();

        Interview saved = interviewRepository.save(interview);

        // Update application status to INTERVIEW_SCHEDULED
        application.setStatus("INTERVIEW_SCHEDULED");
        applicationRepository.save(application);

        // Update candidate status
        Candidate c = candidateRepository.findById(application.getCandidateId()).orElse(null);
        if (c != null) {
            c.setStatus("INTERVIEWING");
            candidateRepository.save(c);
        }

        log.info("Scheduled interview {} for candidate {}", saved.getId(), application.getCandidateId());
        return mapToDTO(saved);
    }

    public InterviewDTO updateInterview(String id, InterviewDTO dto) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + id));

        if (dto.getInterviewType() != null) interview.setInterviewType(dto.getInterviewType());
        if (dto.getMode() != null) interview.setMode(dto.getMode());
        if (dto.getPanelMembers() != null) interview.setPanelMembers(dto.getPanelMembers());
        if (dto.getInterviewTime() != null) interview.setInterviewTime(dto.getInterviewTime());
        if (dto.getLocationOrLink() != null) interview.setLocationOrLink(dto.getLocationOrLink());
        if (dto.getResult() != null) interview.setResult(dto.getResult());

        Interview updated = interviewRepository.save(interview);
        return mapToDTO(updated);
    }

    public InterviewFeedbackDTO submitFeedback(InterviewFeedbackDTO dto) {
        Interview interview = interviewRepository.findById(dto.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + dto.getInterviewId()));

        int tech = dto.getTechnicalRating() != null ? dto.getTechnicalRating() : 3;
        int comm = dto.getCommunicationRating() != null ? dto.getCommunicationRating() : 3;
        int prob = dto.getProblemSolvingRating() != null ? dto.getProblemSolvingRating() : 3;
        int beh = dto.getBehaviorRating() != null ? dto.getBehaviorRating() : 3;

        double overall = (tech + comm + prob + beh) / 4.0;

        InterviewFeedback feedback = InterviewFeedback.builder()
                .interviewId(interview.getId())
                .interviewerId(dto.getInterviewerId())
                .technicalRating(tech)
                .communicationRating(comm)
                .problemSolvingRating(prob)
                .behaviorRating(beh)
                .overallRating(overall)
                .recommendation(dto.getRecommendation() != null ? dto.getRecommendation() : "RECOMMEND")
                .remarks(dto.getRemarks())
                .build();

        InterviewFeedback saved = feedbackRepository.save(feedback);

        // Update interview result based on feedback recommendation if needed
        if ("REJECT".equalsIgnoreCase(dto.getRecommendation())) {
            interview.setResult("FAILED");
        } else if ("RECOMMEND".equalsIgnoreCase(dto.getRecommendation()) || "STRONG_RECOMMEND".equalsIgnoreCase(dto.getRecommendation())) {
            interview.setResult("PASSED");
        }
        interviewRepository.save(interview);

        return InterviewFeedbackDTO.builder()
                .id(saved.getId())
                .interviewId(saved.getInterviewId())
                .interviewerId(saved.getInterviewerId())
                .technicalRating(saved.getTechnicalRating())
                .communicationRating(saved.getCommunicationRating())
                .problemSolvingRating(saved.getProblemSolvingRating())
                .behaviorRating(saved.getBehaviorRating())
                .overallRating(saved.getOverallRating())
                .recommendation(saved.getRecommendation())
                .remarks(saved.getRemarks())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    public List<InterviewFeedbackDTO> getFeedbacksByInterview(String interviewId) {
        return feedbackRepository.findByInterviewId(interviewId).stream().map(f -> InterviewFeedbackDTO.builder()
                .id(f.getId())
                .interviewId(f.getInterviewId())
                .interviewerId(f.getInterviewerId())
                .technicalRating(f.getTechnicalRating())
                .communicationRating(f.getCommunicationRating())
                .problemSolvingRating(f.getProblemSolvingRating())
                .behaviorRating(f.getBehaviorRating())
                .overallRating(f.getOverallRating())
                .recommendation(f.getRecommendation())
                .remarks(f.getRemarks())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build()
        ).collect(Collectors.toList());
    }

    private InterviewDTO mapToDTO(Interview interview) {
        Candidate c = candidateRepository.findById(interview.getCandidateId()).orElse(null);
        JobPosting j = jobPostingRepository.findById(interview.getJobPostingId()).orElse(null);
        List<InterviewFeedbackDTO> feedbacks = getFeedbacksByInterview(interview.getId());

        return InterviewDTO.builder()
                .id(interview.getId())
                .applicationId(interview.getApplicationId())
                .candidateId(interview.getCandidateId())
                .jobPostingId(interview.getJobPostingId())
                .candidateName(c != null ? (c.getFirstName() + " " + c.getLastName()) : "Unknown")
                .jobTitle(j != null ? j.getTitle() : "Unknown")
                .interviewType(interview.getInterviewType())
                .mode(interview.getMode())
                .panelMembers(interview.getPanelMembers())
                .interviewTime(interview.getInterviewTime())
                .locationOrLink(interview.getLocationOrLink())
                .result(interview.getResult())
                .feedbacks(feedbacks)
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .build();
    }
}
