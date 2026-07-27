package com.techknife.crm.service;

import com.techknife.crm.dto.LeadDTO;
import com.techknife.crm.entity.Lead;
import com.techknife.crm.entity.LeadPriority;
import com.techknife.crm.entity.LeadSource;
import com.techknife.crm.entity.LeadStatus;
import com.techknife.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;

    public List<LeadDTO> getAllLeads(String status) {
        List<Lead> leads;
        if (status != null && !status.isEmpty()) {
            try {
                LeadStatus leadStatus = LeadStatus.valueOf(status.toUpperCase());
                leads = leadRepository.findByLeadStatus(leadStatus);
            } catch (IllegalArgumentException e) {
                leads = leadRepository.findAll();
            }
        } else {
            leads = leadRepository.findAll();
        }
        return leads.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public LeadDTO getLeadById(String id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
        return mapToDTO(lead);
    }

    public LeadDTO createLead(LeadDTO dto) {
        // Validation for duplicate Lead
        if (dto.getEmail() != null && leadRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Lead already exists with email: " + dto.getEmail());
        }
        if (dto.getPhone() != null && leadRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Lead already exists with phone: " + dto.getPhone());
        }
        if (dto.getCompanyName() != null && leadRepository.existsByCompanyName(dto.getCompanyName())) {
            throw new RuntimeException("Lead already exists with company name: " + dto.getCompanyName());
        }

        String leadNumber = "LEAD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Lead lead = Lead.builder()
                .leadNumber(leadNumber)
                .companyName(dto.getCompanyName())
                .contactPerson(dto.getContactPerson())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .website(dto.getWebsite())
                .industry(dto.getIndustry())
                .companySize(dto.getCompanySize())
                .country(dto.getCountry())
                .state(dto.getState())
                .city(dto.getCity())
                .leadSource(dto.getLeadSource() != null ? dto.getLeadSource() : LeadSource.WEBSITE)
                .customSource(dto.getCustomSource())
                .priority(dto.getPriority() != null ? dto.getPriority() : LeadPriority.MEDIUM)
                .leadStatus(dto.getLeadStatus() != null ? dto.getLeadStatus() : LeadStatus.NEW)
                .assignedEmployeeId(dto.getAssignedEmployeeId())
                .assignedEmployeeName(dto.getAssignedEmployeeName())
                .expectedBudget(dto.getExpectedBudget())
                .expectedStartDate(dto.getExpectedStartDate())
                .remarks(dto.getRemarks())
                .build();

        Lead saved = leadRepository.save(lead);
        log.info("Created Lead: {} - {}", saved.getLeadNumber(), saved.getCompanyName());
        return mapToDTO(saved);
    }

    public LeadDTO updateLead(String id, LeadDTO dto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        if (dto.getCompanyName() != null) lead.setCompanyName(dto.getCompanyName());
        if (dto.getContactPerson() != null) lead.setContactPerson(dto.getContactPerson());
        if (dto.getEmail() != null) lead.setEmail(dto.getEmail());
        if (dto.getPhone() != null) lead.setPhone(dto.getPhone());
        if (dto.getWebsite() != null) lead.setWebsite(dto.getWebsite());
        if (dto.getIndustry() != null) lead.setIndustry(dto.getIndustry());
        if (dto.getCompanySize() != null) lead.setCompanySize(dto.getCompanySize());
        if (dto.getCountry() != null) lead.setCountry(dto.getCountry());
        if (dto.getState() != null) lead.setState(dto.getState());
        if (dto.getCity() != null) lead.setCity(dto.getCity());
        if (dto.getLeadSource() != null) lead.setLeadSource(dto.getLeadSource());
        if (dto.getCustomSource() != null) lead.setCustomSource(dto.getCustomSource());
        if (dto.getPriority() != null) lead.setPriority(dto.getPriority());
        if (dto.getLeadStatus() != null) lead.setLeadStatus(dto.getLeadStatus());
        if (dto.getAssignedEmployeeId() != null) lead.setAssignedEmployeeId(dto.getAssignedEmployeeId());
        if (dto.getAssignedEmployeeName() != null) lead.setAssignedEmployeeName(dto.getAssignedEmployeeName());
        if (dto.getExpectedBudget() != null) lead.setExpectedBudget(dto.getExpectedBudget());
        if (dto.getExpectedStartDate() != null) lead.setExpectedStartDate(dto.getExpectedStartDate());
        if (dto.getRemarks() != null) lead.setRemarks(dto.getRemarks());

        Lead updated = leadRepository.save(lead);
        return mapToDTO(updated);
    }

    public void deleteLead(String id) {
        if (!leadRepository.existsById(id)) {
            throw new RuntimeException("Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }

    public LeadDTO mapToDTO(Lead lead) {
        return LeadDTO.builder()
                .id(lead.getId())
                .leadNumber(lead.getLeadNumber())
                .companyName(lead.getCompanyName())
                .contactPerson(lead.getContactPerson())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .website(lead.getWebsite())
                .industry(lead.getIndustry())
                .companySize(lead.getCompanySize())
                .country(lead.getCountry())
                .state(lead.getState())
                .city(lead.getCity())
                .leadSource(lead.getLeadSource())
                .customSource(lead.getCustomSource())
                .priority(lead.getPriority())
                .leadStatus(lead.getLeadStatus())
                .assignedEmployeeId(lead.getAssignedEmployeeId())
                .assignedEmployeeName(lead.getAssignedEmployeeName())
                .expectedBudget(lead.getExpectedBudget())
                .expectedStartDate(lead.getExpectedStartDate())
                .remarks(lead.getRemarks())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}
