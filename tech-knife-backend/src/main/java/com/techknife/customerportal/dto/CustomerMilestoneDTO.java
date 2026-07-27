package com.techknife.customerportal.dto;

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
public class CustomerMilestoneDTO {

    private String id;
    private String projectId;
    private String customerAccountId;
    private String milestoneName;
    private String description;
    private String status;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private Double completionPercentage;
    private List<String> deliverables;
    private Instant createdAt;
    private Instant updatedAt;
}
