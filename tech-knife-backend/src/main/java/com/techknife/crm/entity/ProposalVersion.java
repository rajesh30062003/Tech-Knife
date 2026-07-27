package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalVersion {
    private Integer versionNumber;
    private String title;
    private String modifiedBy;
    private Instant modifiedAt;
    private String changeSummary;
    private String documentUrl;
}
