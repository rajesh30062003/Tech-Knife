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
 * MongoDB Document tracking Intern Mentorship assignments and maximum intern limits.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "intern_mentors")
public class InternMentor {

    @Id
    private String id;

    @Indexed
    private String internId;

    @Indexed
    private String mentorId; // Employee ID of the mentor

    private LocalDate assignedDate;

    private LocalDate endDate;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private Integer maxInternLimit = 5;

    private String assignedBy;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
