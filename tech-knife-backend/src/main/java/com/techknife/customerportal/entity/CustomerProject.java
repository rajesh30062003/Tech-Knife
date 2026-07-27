package com.techknife.customerportal.entity;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_projects")
public class CustomerProject {

    @Id
    private String id;

    @Indexed
    private String customerAccountId;

    @Indexed
    private String projectCode;

    private String projectName;

    private String description;

    @Builder.Default
    private String status = "IN_PROGRESS"; // PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private Double progressPercentage = 0.0;

    private String projectManagerName;

    private String projectManagerEmail;

    @Builder.Default
    private List<TeamMemberView> assignedTeam = new ArrayList<>();

    @Builder.Default
    private List<String> techStack = new ArrayList<>();

    private String repositoryUrl;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberView {
        private String name;
        private String role;
        private String avatarUrl;
    }
}
