package com.techknife.recruitment.service;

import com.techknife.recruitment.dto.CandidateDTO;
import com.techknife.recruitment.entity.Candidate;
import com.techknife.recruitment.repository.CandidateRepository;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadRequest;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final FileStorageService fileStorageService;

    public List<CandidateDTO> getAllCandidates(String status, String skill) {
        List<Candidate> candidates;
        if (status != null) {
            candidates = candidateRepository.findByStatus(status);
        } else if (skill != null) {
            candidates = candidateRepository.findBySkillsContainingIgnoreCase(skill);
        } else {
            candidates = candidateRepository.findAll();
        }
        return candidates.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public CandidateDTO getCandidateById(String id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
        return mapToDTO(candidate);
    }

    public CandidateDTO createCandidate(CandidateDTO dto) {
        if (candidateRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Candidate with email " + dto.getEmail() + " already exists");
        }

        String code = "CND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Candidate candidate = Candidate.builder()
                .candidateCode(code)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .experience(dto.getExperience())
                .currentCompany(dto.getCurrentCompany())
                .currentCtc(dto.getCurrentCtc())
                .expectedCtc(dto.getExpectedCtc())
                .noticePeriod(dto.getNoticePeriod())
                .skills(dto.getSkills())
                .resumeUrl(dto.getResumeUrl())
                .portfolioUrl(dto.getPortfolioUrl())
                .linkedInUrl(dto.getLinkedInUrl())
                .gitHubUrl(dto.getGitHubUrl())
                .status(dto.getStatus() != null ? dto.getStatus() : "NEW")
                .build();

        Candidate saved = candidateRepository.save(candidate);
        log.info("Registered new candidate: {} ({})", saved.getCandidateCode(), saved.getEmail());
        return mapToDTO(saved);
    }

    public CandidateDTO updateCandidate(String id, CandidateDTO dto) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));

        if (dto.getFirstName() != null) candidate.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) candidate.setLastName(dto.getLastName());
        if (dto.getPhone() != null) candidate.setPhone(dto.getPhone());
        if (dto.getAddress() != null) candidate.setAddress(dto.getAddress());
        if (dto.getExperience() != null) candidate.setExperience(dto.getExperience());
        if (dto.getCurrentCompany() != null) candidate.setCurrentCompany(dto.getCurrentCompany());
        if (dto.getCurrentCtc() != null) candidate.setCurrentCtc(dto.getCurrentCtc());
        if (dto.getExpectedCtc() != null) candidate.setExpectedCtc(dto.getExpectedCtc());
        if (dto.getNoticePeriod() != null) candidate.setNoticePeriod(dto.getNoticePeriod());
        if (dto.getSkills() != null) candidate.setSkills(dto.getSkills());
        if (dto.getResumeUrl() != null) candidate.setResumeUrl(dto.getResumeUrl());
        if (dto.getPortfolioUrl() != null) candidate.setPortfolioUrl(dto.getPortfolioUrl());
        if (dto.getLinkedInUrl() != null) candidate.setLinkedInUrl(dto.getLinkedInUrl());
        if (dto.getGitHubUrl() != null) candidate.setGitHubUrl(dto.getGitHubUrl());
        if (dto.getStatus() != null) candidate.setStatus(dto.getStatus());

        Candidate updated = candidateRepository.save(candidate);
        return mapToDTO(updated);
    }

    public CandidateDTO uploadResume(String id, MultipartFile file) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));

        FileUploadRequest uploadRequest = FileUploadRequest.builder()
                .file(file)
                .folder("resumes")
                .description("Resume for candidate " + candidate.getCandidateCode())
                .build();

        FileUploadResponse response = fileStorageService.uploadFile(uploadRequest);
        candidate.setResumeUrl(response.getSecureUrl());
        Candidate saved = candidateRepository.save(candidate);
        return mapToDTO(saved);
    }

    public void deleteCandidate(String id) {
        if (!candidateRepository.existsById(id)) {
            throw new RuntimeException("Candidate not found with id: " + id);
        }
        candidateRepository.deleteById(id);
    }

    public CandidateDTO mapToDTO(Candidate c) {
        return CandidateDTO.builder()
                .id(c.getId())
                .candidateCode(c.getCandidateCode())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .experience(c.getExperience())
                .currentCompany(c.getCurrentCompany())
                .currentCtc(c.getCurrentCtc())
                .expectedCtc(c.getExpectedCtc())
                .noticePeriod(c.getNoticePeriod())
                .skills(c.getSkills())
                .resumeUrl(c.getResumeUrl())
                .portfolioUrl(c.getPortfolioUrl())
                .linkedInUrl(c.getLinkedInUrl())
                .gitHubUrl(c.getGitHubUrl())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
