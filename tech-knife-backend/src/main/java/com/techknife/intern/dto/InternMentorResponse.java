package com.techknife.intern.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternMentorResponse {
    private String id;
    private String internId;
    private String mentorId;
    private String mentorName;
    private LocalDate assignedDate;
    private LocalDate endDate;
    private boolean active;
    private Integer maxInternLimit;
    private String assignedBy;
    private String remarks;
    private Instant createdAt;
}
