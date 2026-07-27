package com.techknife.project.resource.dto;

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
public class ResourceAllocationDTO {

    private String id;
    private String employeeId;
    private String projectId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalCapacityHours;
    private Double assignedHours;
    private Double utilizationPercentage;
    private boolean overAllocated;
    private boolean underAllocated;
    private Instant createdAt;
    private Instant updatedAt;
}
