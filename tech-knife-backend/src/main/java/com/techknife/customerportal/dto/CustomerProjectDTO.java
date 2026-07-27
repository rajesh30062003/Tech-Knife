package com.techknife.customerportal.dto;

import com.techknife.customerportal.entity.CustomerProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProjectDTO {

    private String id;
    private String customerAccountId;
    private String projectCode;
    private String projectName;
    private String description;
    private String status;
    private String priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double progressPercentage;
    private String projectManagerName;
    private String projectManagerEmail;
    private List<CustomerProject.TeamMemberView> assignedTeam;
    private List<String> techStack;
    private String repositoryUrl;
    private long sharedDocumentsCount;
    private long openTicketsCount;
    private List<CustomerMilestoneDTO> milestones;
    private List<SharedDocumentDTO> sharedDocuments;
    private Instant createdAt;
    private Instant updatedAt;
}
