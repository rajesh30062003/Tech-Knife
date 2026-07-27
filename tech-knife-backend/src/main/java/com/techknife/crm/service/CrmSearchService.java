package com.techknife.crm.service;

import com.techknife.crm.dto.CrmSearchDTO;
import com.techknife.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrmSearchService {

    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;
    private final OpportunityRepository opportunityRepository;
    private final QuotationRepository quotationRepository;
    private final ProposalRepository proposalRepository;
    private final ContractRepository contractRepository;

    private final LeadService leadService;
    private final CustomerService customerService;
    private final OpportunityService opportunityService;
    private final QuotationService quotationService;
    private final ProposalService proposalService;
    private final ContractService contractService;

    public CrmSearchDTO search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return CrmSearchDTO.builder()
                    .query(query)
                    .leads(List.of())
                    .customers(List.of())
                    .opportunities(List.of())
                    .quotations(List.of())
                    .proposals(List.of())
                    .contracts(List.of())
                    .build();
        }

        String q = query.trim();

        var leads = leadRepository.findByCompanyNameContainingIgnoreCaseOrContactPersonContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q)
                .stream().map(leadService::mapToDTO).collect(Collectors.toList());

        var customers = customerRepository.findByCompanyNameContainingIgnoreCaseOrGstNumberContainingIgnoreCase(q, q)
                .stream().map(customerService::mapToDTO).collect(Collectors.toList());

        var opportunities = opportunityRepository.findAll().stream()
                .filter(o -> (o.getTitle() != null && o.getTitle().toLowerCase().contains(q.toLowerCase()))
                        || (o.getOpportunityNumber() != null && o.getOpportunityNumber().toLowerCase().contains(q.toLowerCase())))
                .map(opportunityService::mapToDTO).collect(Collectors.toList());

        var quotations = quotationRepository.findAll().stream()
                .filter(qt -> (qt.getTitle() != null && qt.getTitle().toLowerCase().contains(q.toLowerCase()))
                        || (qt.getQuotationNumber() != null && qt.getQuotationNumber().toLowerCase().contains(q.toLowerCase())))
                .map(quotationService::mapToDTO).collect(Collectors.toList());

        var proposals = proposalRepository.findAll().stream()
                .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(q.toLowerCase()))
                        || (p.getProposalNumber() != null && p.getProposalNumber().toLowerCase().contains(q.toLowerCase())))
                .map(proposalService::mapToDTO).collect(Collectors.toList());

        var contracts = contractRepository.findAll().stream()
                .filter(c -> (c.getTitle() != null && c.getTitle().toLowerCase().contains(q.toLowerCase()))
                        || (c.getContractNumber() != null && c.getContractNumber().toLowerCase().contains(q.toLowerCase())))
                .map(contractService::mapToDTO).collect(Collectors.toList());

        return CrmSearchDTO.builder()
                .query(query)
                .leads(leads)
                .customers(customers)
                .opportunities(opportunities)
                .quotations(quotations)
                .proposals(proposals)
                .contracts(contracts)
                .build();
    }
}
