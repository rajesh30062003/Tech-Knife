package com.techknife.crm.service;

import com.techknife.crm.dto.ContractDTO;
import com.techknife.crm.entity.Contract;
import com.techknife.crm.repository.ContractRepository;
import com.techknife.storage.FileStorageService;
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
public class ContractService {

    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;

    public List<ContractDTO> getAllContracts(String status) {
        List<Contract> contracts;
        if (status != null && !status.isEmpty()) {
            contracts = contractRepository.findByStatus(status.toUpperCase());
        } else {
            contracts = contractRepository.findAll();
        }
        return contracts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ContractDTO getContractById(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));
        return mapToDTO(contract);
    }

    public ContractDTO createContract(ContractDTO dto) {
        String contractNum = "CNT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Contract contract = Contract.builder()
                .contractNumber(contractNum)
                .customerId(dto.getCustomerId())
                .opportunityId(dto.getOpportunityId())
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .renewalDate(dto.getRenewalDate())
                .contractValue(dto.getContractValue())
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .digitalSignatureReady(dto.getDigitalSignatureReady() != null ? dto.getDigitalSignatureReady() : false)
                .digitalSignatureUrl(dto.getDigitalSignatureUrl())
                .termsAndConditions(dto.getTermsAndConditions())
                .build();

        Contract saved = contractRepository.save(contract);
        log.info("Created Contract: {}", saved.getContractNumber());
        return mapToDTO(saved);
    }

    public ContractDTO updateContract(String id, ContractDTO dto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));

        if (dto.getTitle() != null) contract.setTitle(dto.getTitle());
        if (dto.getStartDate() != null) contract.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) contract.setEndDate(dto.getEndDate());
        if (dto.getRenewalDate() != null) contract.setRenewalDate(dto.getRenewalDate());
        if (dto.getContractValue() != null) contract.setContractValue(dto.getContractValue());
        if (dto.getStatus() != null) contract.setStatus(dto.getStatus());
        if (dto.getDigitalSignatureReady() != null) contract.setDigitalSignatureReady(dto.getDigitalSignatureReady());
        if (dto.getTermsAndConditions() != null) contract.setTermsAndConditions(dto.getTermsAndConditions());

        Contract updated = contractRepository.save(contract);
        return mapToDTO(updated);
    }

    public ContractDTO uploadContractDocument(String id, MultipartFile file) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));

        FileUploadResponse response = fileStorageService.uploadDocument(file, "crm/contracts");
        contract.setContractDocumentUrl(response.getSecureUrl());

        Contract saved = contractRepository.save(contract);
        return mapToDTO(saved);
    }

    public void deleteContract(String id) {
        if (!contractRepository.existsById(id)) {
            throw new RuntimeException("Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
    }

    public ContractDTO mapToDTO(Contract c) {
        return ContractDTO.builder()
                .id(c.getId())
                .contractNumber(c.getContractNumber())
                .customerId(c.getCustomerId())
                .opportunityId(c.getOpportunityId())
                .title(c.getTitle())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .renewalDate(c.getRenewalDate())
                .contractValue(c.getContractValue())
                .status(c.getStatus())
                .digitalSignatureReady(c.getDigitalSignatureReady())
                .digitalSignatureUrl(c.getDigitalSignatureUrl())
                .contractDocumentUrl(c.getContractDocumentUrl())
                .termsAndConditions(c.getTermsAndConditions())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
