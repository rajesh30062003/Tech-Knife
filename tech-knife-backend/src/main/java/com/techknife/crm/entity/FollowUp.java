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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_followups")
public class FollowUp {

    @Id
    private String id;

    private String entityType; // LEAD, CUSTOMER, OPPORTUNITY

    @Indexed
    private String entityId;

    private String title;

    private String description;

    private Instant reminderDate;

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @Builder.Default
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, MISSED

    private String assignedEmployeeId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
