package com.techknife.crm.service;

import com.techknife.crm.dto.ProposalDTO;
import com.techknife.crm.entity.Proposal;
import com.techknife.crm.entity.ProposalVersion;
import com.techknife.crm.repository.ProposalRepository;
import com.techknife.storage.FileStorageService;
import com.techknife.storage.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final FileStorageService fileStorageService;

    public List<ProposalDTO> getAllProposals(String status) {
        List<Proposal> proposals;
        if (status != null && !status.isEmpty()) {
            proposals = proposalRepository.findByStatus(status.toUpperCase());
        } else {
            proposals = proposalRepository.findAll();
        }
        return proposals.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProposalDTO getProposalById(String id) {
        Proposal p = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + id));
        return mapToDTO(p);
    }

    public ProposalDTO createProposal(ProposalDTO dto) {
        String propNumber = "PRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ProposalVersion initialVersion = ProposalVersion.builder()
                .versionNumber(1)
                .title(dto.getTitle())
                .modifiedBy("System")
                .modifiedAt(Instant.now())
                .changeSummary("Initial Proposal Creation")
                .build();

        List<ProposalVersion> history = new ArrayList<>();
        history.add(initialVersion);

        Proposal p = Proposal.builder()
                .proposalNumber(propNumber)
                .opportunityId(dto.getOpportunityId())
                .customerId(dto.getCustomerId())
                .leadId(dto.getLeadId())
                .title(dto.getTitle())
                .executiveSummary(dto.getExecutiveSummary())
                .projectScope(dto.getProjectScope())
                .deliverables(dto.getDeliverables() != null ? dto.getDeliverables() : List.of())
                .timeline(dto.getTimeline())
                .commercialTerms(dto.getCommercialTerms())
                .attachments(dto.getAttachments() != null ? dto.getAttachments() : List.of())
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .currentVersion(1)
                .versionHistory(history)
                .build();

        Proposal saved = proposalRepository.save(p);
        log.info("Created Proposal: {}", saved.getProposalNumber());
        return mapToDTO(saved);
    }

    public ProposalDTO updateProposal(String id, ProposalDTO dto, String modifiedBy, String changeSummary) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + id));

        if (dto.getTitle() != null) proposal.setTitle(dto.getTitle());
        if (dto.getExecutiveSummary() != null) proposal.setExecutiveSummary(dto.getExecutiveSummary());
        if (dto.getProjectScope() != null) proposal.setProjectScope(dto.getProjectScope());
        if (dto.getDeliverables() != null) proposal.setDeliverables(dto.getDeliverables());
        if (dto.getTimeline() != null) proposal.setTimeline(dto.getTimeline());
        if (dto.getCommercialTerms() != null) proposal.setCommercialTerms(dto.getCommercialTerms());
        if (dto.getStatus() != null) proposal.setStatus(dto.getStatus());

        int newVersionNumber = (proposal.getCurrentVersion() != null ? proposal.getCurrentVersion() : 1) + 1;
        proposal.setCurrentVersion(newVersionNumber);

        ProposalVersion newVersion = ProposalVersion.builder()
                .versionNumber(newVersionNumber)
                .title(proposal.getTitle())
                .modifiedBy(modifiedBy != null ? modifiedBy : "User")
                .modifiedAt(Instant.now())
                .changeSummary(changeSummary != null ? changeSummary : "Proposal Updated")
                .build();

        if (proposal.getVersionHistory() == null) {
            proposal.setVersionHistory(new ArrayList<>());
        }
        proposal.getVersionHistory().add(newVersion);

        Proposal updated = proposalRepository.save(proposal);
        return mapToDTO(updated);
    }

    public ProposalDTO uploadAttachment(String id, MultipartFile file) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + id));

        FileUploadResponse response = fileStorageService.uploadDocument(file, "crm/proposals");
        if (proposal.getAttachments() == null) {
            proposal.setAttachments(new ArrayList<>());
        }
        proposal.getAttachments().add(response.getSecureUrl());

        Proposal saved = proposalRepository.save(proposal);
        return mapToDTO(saved);
    }

    public void deleteProposal(String id) {
        if (!proposalRepository.existsById(id)) {
            throw new RuntimeException("Proposal not found with id: " + id);
        }
        proposalRepository.deleteById(id);
    }

    public ProposalDTO mapToDTO(Proposal p) {
        return ProposalDTO.builder()
                .id(p.getId())
                .proposalNumber(p.getProposalNumber())
                .opportunityId(p.getOpportunityId())
                .customerId(p.getCustomerId())
                .leadId(p.getLeadId())
                .title(p.getTitle())
                .executiveSummary(p.getExecutiveSummary())
                .projectScope(p.getProjectScope())
                .deliverables(p.getDeliverables())
                .timeline(p.getTimeline())
                .commercialTerms(p.getCommercialTerms())
                .attachments(p.getAttachments())
                .status(p.getStatus())
                .currentVersion(p.getCurrentVersion())
                .versionHistory(p.getVersionHistory())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
