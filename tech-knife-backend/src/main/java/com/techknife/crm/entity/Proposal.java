package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_proposals")
public class Proposal {

    @Id
    private String id;

    @Indexed(unique = true)
    private String proposalNumber;

    @Indexed
    private String opportunityId;

    @Indexed
    private String customerId;

    @Indexed
    private String leadId;

    private String title;

    private String executiveSummary;

    private String projectScope;

    @Builder.Default
    private List<String> deliverables = new ArrayList<>();

    private String timeline;

    private String commercialTerms;

    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, SENT, ACCEPTED, REJECTED, REVISED

    @Builder.Default
    private Integer currentVersion = 1;

    @Builder.Default
    private List<ProposalVersion> versionHistory = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
