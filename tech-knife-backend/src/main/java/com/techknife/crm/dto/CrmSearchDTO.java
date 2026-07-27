package com.techknife.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmSearchDTO {
    private String query;
    private List<LeadDTO> leads;
    private List<CustomerDTO> customers;
    private List<OpportunityDTO> opportunities;
    private List<QuotationDTO> quotations;
    private List<ProposalDTO> proposals;
    private List<ContractDTO> contracts;
}
