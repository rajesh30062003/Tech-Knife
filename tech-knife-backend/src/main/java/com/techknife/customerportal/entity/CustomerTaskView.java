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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_tasks")
public class CustomerTaskView {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String milestoneId;

    @Indexed
    private String customerAccountId;

    private String taskName;

    private String description;

    @Builder.Default
    private String status = "PENDING"; // PENDING, IN_PROGRESS, IN_REVIEW, COMPLETED

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    private LocalDate dueDate;

    private String assigneeName;

    private Instant completedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
