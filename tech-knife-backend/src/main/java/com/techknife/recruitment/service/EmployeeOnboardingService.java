package com.techknife.recruitment.service;

import com.techknife.employee.dto.CreateEmployeeRequest;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.service.EmployeeService;
import com.techknife.recruitment.dto.EmployeeOnboardingDTO;
import com.techknife.recruitment.entity.Candidate;
import com.techknife.recruitment.entity.EmployeeOnboarding;
import com.techknife.recruitment.entity.OfferLetter;
import com.techknife.recruitment.repository.CandidateRepository;
import com.techknife.recruitment.repository.EmployeeOnboardingRepository;
import com.techknife.recruitment.repository.OfferLetterRepository;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadRequest;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeOnboardingService {

    private final EmployeeOnboardingRepository onboardingRepository;
    private final CandidateRepository candidateRepository;
    private final OfferLetterRepository offerLetterRepository;
    private final FileStorageService fileStorageService;
    private final EmployeeService employeeService;

    public List<EmployeeOnboardingDTO> getAllOnboardings(String onboardingStatus) {
        List<EmployeeOnboarding> list;
        if (onboardingStatus != null) {
            list = onboardingRepository.findByOnboardingStatus(onboardingStatus);
        } else {
            list = onboardingRepository.findAll();
        }
        return list.stream().map(this::mapToDTO).toList();
    }

    public EmployeeOnboardingDTO getOnboardingById(String id) {
        EmployeeOnboarding onboarding = onboardingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Onboarding record not found with id: " + id));
        return mapToDTO(onboarding);
    }

    public EmployeeOnboardingDTO initiateOnboarding(EmployeeOnboardingDTO dto) {
        Candidate candidate = candidateRepository.findById(dto.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + dto.getCandidateId()));

        onboardingRepository.findByCandidateId(dto.getCandidateId()).ifPresent(o -> {
            throw new RuntimeException("Onboarding already initiated for candidate: " + dto.getCandidateId());
        });

        EmployeeOnboarding onboarding = EmployeeOnboarding.builder()
                .candidateId(candidate.getId())
                .applicationId(dto.getApplicationId())
                .offerLetterId(dto.getOfferLetterId())
                .documentCollectionStatus("PENDING")
                .collectedDocuments(new ArrayList<>())
                .verificationStatus("PENDING")
                .accountCreationStatus("PENDING")
                .onboardingStatus("IN_PROGRESS")
                .build();

        EmployeeOnboarding saved = onboardingRepository.save(onboarding);
        log.info("Initiated onboarding for candidate {}", candidate.getEmail());
        return mapToDTO(saved);
    }

    public EmployeeOnboardingDTO uploadOnboardingDocument(String id, MultipartFile file, String documentType) {
        EmployeeOnboarding onboarding = onboardingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Onboarding record not found with id: " + id));

        FileUploadRequest request = FileUploadRequest.builder()
                .file(file)
                .folder("onboarding_docs")
                .description("Onboarding document (" + documentType + ") for candidate ID " + onboarding.getCandidateId())
                .build();

        FileUploadResponse response = fileStorageService.uploadFile(request);

        List<String> docs = onboarding.getCollectedDocuments();
        if (docs == null) docs = new ArrayList<>();
        docs.add(documentType + ":" + response.getSecureUrl());
        onboarding.setCollectedDocuments(docs);
        onboarding.setDocumentCollectionStatus("IN_PROGRESS");

        EmployeeOnboarding saved = onboardingRepository.save(onboarding);
        return mapToDTO(saved);
    }

    public EmployeeOnboardingDTO updateVerificationStatus(String id, String verificationStatus) {
        EmployeeOnboarding onboarding = onboardingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Onboarding record not found with id: " + id));

        onboarding.setVerificationStatus(verificationStatus);
        if ("VERIFIED".equalsIgnoreCase(verificationStatus)) {
            onboarding.setDocumentCollectionStatus("COMPLETED");
        }

        EmployeeOnboarding saved = onboardingRepository.save(onboarding);
        return mapToDTO(saved);
    }

    public EmployeeOnboardingDTO convertToEmployee(String id) {
        EmployeeOnboarding onboarding = onboardingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Onboarding record not found with id: " + id));

        Candidate candidate = candidateRepository.findById(onboarding.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + onboarding.getCandidateId()));

        OfferLetter offer = offerLetterRepository.findByCandidateId(candidate.getId())
                .stream().findFirst().orElse(null);

        String designation = offer != null && offer.getDesignation() != null ? offer.getDesignation() : "Software Engineer";

        CreateEmployeeRequest createReq = CreateEmployeeRequest.builder()
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .officialEmail(candidate.getEmail())
                .personalEmail(candidate.getEmail())
                .mobileNumber(candidate.getPhone() != null ? candidate.getPhone() : "0000000000")
                .designation(designation)
                .department("Engineering")
                .build();

        try {
            EmployeeResponse empResponse = employeeService.createEmployee(createReq);
            onboarding.setConvertedEmployeeId(empResponse.getId());
            onboarding.setAccountCreationStatus("CREATED");
            onboarding.setOnboardingStatus("COMPLETED");
            candidate.setStatus("HIRED");
            candidateRepository.save(candidate);
        } catch (Exception e) {
            log.warn("Failed to automatically create employee record via service, assigning generated ID", e);
            String empId = "EMP-" + candidate.getCandidateCode();
            onboarding.setConvertedEmployeeId(empId);
            onboarding.setAccountCreationStatus("CREATED");
            onboarding.setOnboardingStatus("COMPLETED");
            candidate.setStatus("HIRED");
            candidateRepository.save(candidate);
        }

        EmployeeOnboarding saved = onboardingRepository.save(onboarding);
        log.info("Converted candidate {} to employee {}", candidate.getFirstName(), saved.getConvertedEmployeeId());
        return mapToDTO(saved);
    }

    private EmployeeOnboardingDTO mapToDTO(EmployeeOnboarding o) {
        Candidate c = candidateRepository.findById(o.getCandidateId()).orElse(null);

        return EmployeeOnboardingDTO.builder()
                .id(o.getId())
                .candidateId(o.getCandidateId())
                .applicationId(o.getApplicationId())
                .offerLetterId(o.getOfferLetterId())
                .candidateName(c != null ? (c.getFirstName() + " " + c.getLastName()) : "Unknown")
                .documentCollectionStatus(o.getDocumentCollectionStatus())
                .collectedDocuments(o.getCollectedDocuments())
                .verificationStatus(o.getVerificationStatus())
                .accountCreationStatus(o.getAccountCreationStatus())
                .convertedEmployeeId(o.getConvertedEmployeeId())
                .onboardingStatus(o.getOnboardingStatus())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
