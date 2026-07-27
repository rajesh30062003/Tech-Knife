package com.techknife.crm.dto;

import com.techknife.crm.entity.SalesStage;
import jakarta.validation.constraints.NotBlank;
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
public class OpportunityDTO {
    private String id;
    private String opportunityNumber;

    @NotBlank(message = "Title is required")
    private String title;

    private String leadId;
    private String customerId;

    private SalesStage salesStage;
    private Double estimatedRevenue;
    private Double probabilityPercentage;
    private LocalDate expectedClosingDate;

    private String competitor;
    private String decisionMaker;
    private String nextAction;
    private String assignedEmployeeId;
    private String status;

    private Instant createdAt;
    private Instant updatedAt;
}
