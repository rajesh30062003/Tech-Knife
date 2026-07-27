package com.techknife.intern.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * MongoDB Document for Intern Task Management.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "intern_tasks")
public class InternTask {

    @Id
    private String id;

    @Indexed
    private String internId;

    @Indexed
    private String mentorId;

    private String title;

    private String description;

    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Builder.Default
    private Integer progressPercentage = 0; // 0 to 100

    private LocalDate deadline;

    private LocalDate assignedDate;

    private LocalDate completionDate;

    private String reviewRemarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
