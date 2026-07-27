package com.techknife.crm.dto;

import com.techknife.crm.entity.ProposalVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalDTO {
    private String id;
    private String proposalNumber;
    private String opportunityId;
    private String customerId;
    private String leadId;
    private String title;

    private String executiveSummary;
    private String projectScope;
    private List<String> deliverables;
    private String timeline;
    private String commercialTerms;
    private List<String> attachments;

    private String status;
    private Integer currentVersion;
    private List<ProposalVersion> versionHistory;

    private Instant createdAt;
    private Instant updatedAt;
}
