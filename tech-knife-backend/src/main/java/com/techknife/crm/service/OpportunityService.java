package com.techknife.crm.service;

import com.techknife.crm.dto.OpportunityDTO;
import com.techknife.crm.entity.Opportunity;
import com.techknife.crm.entity.SalesStage;
import com.techknife.crm.repository.OpportunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;

    public List<OpportunityDTO> getAllOpportunities(String stage, String status) {
        List<Opportunity> opportunities;
        if (stage != null && !stage.isEmpty()) {
            try {
                SalesStage salesStage = SalesStage.valueOf(stage.toUpperCase());
                opportunities = opportunityRepository.findBySalesStage(salesStage);
            } catch (IllegalArgumentException e) {
                opportunities = opportunityRepository.findAll();
            }
        } else if (status != null && !status.isEmpty()) {
            opportunities = opportunityRepository.findByStatus(status.toUpperCase());
        } else {
            opportunities = opportunityRepository.findAll();
        }
        return opportunities.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public OpportunityDTO getOpportunityById(String id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id: " + id));
        return mapToDTO(opp);
    }

    public OpportunityDTO createOpportunity(OpportunityDTO dto) {
        String oppNumber = "OPP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Opportunity opp = Opportunity.builder()
                .opportunityNumber(oppNumber)
                .title(dto.getTitle())
                .leadId(dto.getLeadId())
                .customerId(dto.getCustomerId())
                .salesStage(dto.getSalesStage() != null ? dto.getSalesStage() : SalesStage.LEAD)
                .estimatedRevenue(dto.getEstimatedRevenue())
                .probabilityPercentage(dto.getProbabilityPercentage() != null ? dto.getProbabilityPercentage() : 50.0)
                .expectedClosingDate(dto.getExpectedClosingDate())
                .competitor(dto.getCompetitor())
                .decisionMaker(dto.getDecisionMaker())
                .nextAction(dto.getNextAction())
                .assignedEmployeeId(dto.getAssignedEmployeeId())
                .status(dto.getStatus() != null ? dto.getStatus() : "OPEN")
                .build();

        Opportunity saved = opportunityRepository.save(opp);
        log.info("Created Opportunity: {} - {}", saved.getOpportunityNumber(), saved.getTitle());
        return mapToDTO(saved);
    }

    public OpportunityDTO updateOpportunity(String id, OpportunityDTO dto) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found with id: " + id));

        if (dto.getTitle() != null) opp.setTitle(dto.getTitle());
        if (dto.getLeadId() != null) opp.setLeadId(dto.getLeadId());
        if (dto.getCustomerId() != null) opp.setCustomerId(dto.getCustomerId());
        if (dto.getSalesStage() != null) opp.setSalesStage(dto.getSalesStage());
        if (dto.getEstimatedRevenue() != null) opp.setEstimatedRevenue(dto.getEstimatedRevenue());
        if (dto.getProbabilityPercentage() != null) opp.setProbabilityPercentage(dto.getProbabilityPercentage());
        if (dto.getExpectedClosingDate() != null) opp.setExpectedClosingDate(dto.getExpectedClosingDate());
        if (dto.getCompetitor() != null) opp.setCompetitor(dto.getCompetitor());
        if (dto.getDecisionMaker() != null) opp.setDecisionMaker(dto.getDecisionMaker());
        if (dto.getNextAction() != null) opp.setNextAction(dto.getNextAction());
        if (dto.getAssignedEmployeeId() != null) opp.setAssignedEmployeeId(dto.getAssignedEmployeeId());
        if (dto.getStatus() != null) opp.setStatus(dto.getStatus());

        Opportunity updated = opportunityRepository.save(opp);
        return mapToDTO(updated);
    }

    public void deleteOpportunity(String id) {
        if (!opportunityRepository.existsById(id)) {
            throw new RuntimeException("Opportunity not found with id: " + id);
        }
        opportunityRepository.deleteById(id);
    }

    public OpportunityDTO mapToDTO(Opportunity opp) {
        return OpportunityDTO.builder()
                .id(opp.getId())
                .opportunityNumber(opp.getOpportunityNumber())
                .title(opp.getTitle())
                .leadId(opp.getLeadId())
                .customerId(opp.getCustomerId())
                .salesStage(opp.getSalesStage())
                .estimatedRevenue(opp.getEstimatedRevenue())
                .probabilityPercentage(opp.getProbabilityPercentage())
                .expectedClosingDate(opp.getExpectedClosingDate())
                .competitor(opp.getCompetitor())
                .decisionMaker(opp.getDecisionMaker())
                .nextAction(opp.getNextAction())
                .assignedEmployeeId(opp.getAssignedEmployeeId())
                .status(opp.getStatus())
                .createdAt(opp.getCreatedAt())
                .updatedAt(opp.getUpdatedAt())
                .build();
    }
}
