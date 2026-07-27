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
@Document(collection = "customer_milestones")
public class CustomerMilestone {

    @Id
    private String id;

    @Indexed
    private String projectId;

    @Indexed
    private String customerAccountId;

    private String milestoneName;

    private String description;

    @Builder.Default
    private String status = "PLANNED"; // PLANNED, IN_PROGRESS, COMPLETED, OVERDUE

    private LocalDate dueDate;

    private LocalDate completedDate;

    @Builder.Default
    private Double completionPercentage = 0.0;

    @Builder.Default
    private List<String> deliverables = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
