package com.techknife.recruitment.service;

import com.techknife.recruitment.dto.OfferLetterDTO;
import com.techknife.recruitment.entity.Application;
import com.techknife.recruitment.entity.Candidate;
import com.techknife.recruitment.entity.JobPosting;
import com.techknife.recruitment.entity.OfferLetter;
import com.techknife.recruitment.repository.ApplicationRepository;
import com.techknife.recruitment.repository.CandidateRepository;
import com.techknife.recruitment.repository.JobPostingRepository;
import com.techknife.recruitment.repository.OfferLetterRepository;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadRequest;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferLetterService {

    private final OfferLetterRepository offerLetterRepository;
    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;
    private final FileStorageService fileStorageService;

    public List<OfferLetterDTO> getAllOfferLetters(String acceptanceStatus) {
        List<OfferLetter> offers;
        if (acceptanceStatus != null) {
            offers = offerLetterRepository.findByAcceptanceStatus(acceptanceStatus);
        } else {
            offers = offerLetterRepository.findAll();
        }
        return offers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public OfferLetterDTO getOfferLetterById(String id) {
        OfferLetter offer = offerLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer letter not found with id: " + id));
        return mapToDTO(offer);
    }

    public OfferLetterDTO getOfferLetterByApplicationId(String applicationId) {
        OfferLetter offer = offerLetterRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("Offer letter not found for application: " + applicationId));
        return mapToDTO(offer);
    }

    public OfferLetterDTO generateOfferLetter(OfferLetterDTO dto) {
        Application application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + dto.getApplicationId()));

        Candidate candidate = candidateRepository.findById(application.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + application.getCandidateId()));

        JobPosting jobPosting = jobPostingRepository.findById(application.getJobPostingId())
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + application.getJobPostingId()));

        OfferLetter offerLetter = OfferLetter.builder()
                .applicationId(application.getId())
                .candidateId(candidate.getId())
                .jobPostingId(jobPosting.getId())
                .salary(dto.getSalary())
                .designation(dto.getDesignation() != null ? dto.getDesignation() : jobPosting.getDesignation())
                .joiningDate(dto.getJoiningDate() != null ? dto.getJoiningDate() : LocalDate.now().plusDays(30))
                .validityDate(dto.getValidityDate() != null ? dto.getValidityDate() : LocalDate.now().plusDays(7))
                .acceptanceStatus("PENDING")
                .offerLetterUrl(dto.getOfferLetterUrl())
                .build();

        OfferLetter saved = offerLetterRepository.save(offerLetter);

        // Update application and candidate statuses
        application.setStatus("OFFER_SENT");
        applicationRepository.save(application);

        candidate.setStatus("OFFERED");
        candidateRepository.save(candidate);

        log.info("Generated offer letter {} for candidate {}", saved.getId(), candidate.getEmail());
        return mapToDTO(saved);
    }

    public OfferLetterDTO uploadOfferDocument(String id, MultipartFile file) {
        OfferLetter offer = offerLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer letter not found with id: " + id));

        FileUploadRequest request = FileUploadRequest.builder()
                .file(file)
                .folder("offer_letters")
                .description("Offer Letter document for ID " + id)
                .build();

        FileUploadResponse response = fileStorageService.uploadFile(request);
        offer.setOfferLetterUrl(response.getSecureUrl());

        OfferLetter saved = offerLetterRepository.save(offer);
        return mapToDTO(saved);
    }

    public OfferLetterDTO respondToOffer(String id, String responseStatus) {
        OfferLetter offer = offerLetterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer letter not found with id: " + id));

        offer.setAcceptanceStatus(responseStatus);
        OfferLetter updated = offerLetterRepository.save(offer);

        Application application = applicationRepository.findById(updated.getApplicationId()).orElse(null);
        Candidate candidate = candidateRepository.findById(updated.getCandidateId()).orElse(null);

        if ("ACCEPTED".equalsIgnoreCase(responseStatus)) {
            if (application != null) {
                application.setStatus("OFFER_ACCEPTED");
                applicationRepository.save(application);
            }
            if (candidate != null) {
                candidate.setStatus("HIRED");
                candidateRepository.save(candidate);
            }
        } else if ("DECLINED".equalsIgnoreCase(responseStatus)) {
            if (application != null) {
                application.setStatus("OFFER_DECLINED");
                applicationRepository.save(application);
            }
            if (candidate != null) {
                candidate.setStatus("REJECTED");
                candidateRepository.save(candidate);
            }
        }

        return mapToDTO(updated);
    }

    private OfferLetterDTO mapToDTO(OfferLetter offer) {
        Candidate c = candidateRepository.findById(offer.getCandidateId()).orElse(null);
        JobPosting j = jobPostingRepository.findById(offer.getJobPostingId()).orElse(null);

        return OfferLetterDTO.builder()
                .id(offer.getId())
                .applicationId(offer.getApplicationId())
                .candidateId(offer.getCandidateId())
                .jobPostingId(offer.getJobPostingId())
                .candidateName(c != null ? (c.getFirstName() + " " + c.getLastName()) : "Unknown")
                .candidateEmail(c != null ? c.getEmail() : null)
                .jobTitle(j != null ? j.getTitle() : "Unknown")
                .salary(offer.getSalary())
                .designation(offer.getDesignation())
                .joiningDate(offer.getJoiningDate())
                .validityDate(offer.getValidityDate())
                .acceptanceStatus(offer.getAcceptanceStatus())
                .offerLetterUrl(offer.getOfferLetterUrl())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }
}
