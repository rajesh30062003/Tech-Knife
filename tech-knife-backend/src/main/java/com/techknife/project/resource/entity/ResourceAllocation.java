package com.techknife.project.resource.entity;

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
@Document(collection = "resource_allocations")
public class ResourceAllocation {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    @Indexed
    private String projectId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double totalCapacityHours;

    private Double assignedHours;

    private Double utilizationPercentage;

    @Builder.Default
    private boolean overAllocated = false;

    @Builder.Default
    private boolean underAllocated = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
