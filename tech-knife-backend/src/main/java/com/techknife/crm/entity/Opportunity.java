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
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_opportunities")
public class Opportunity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String opportunityNumber;

    private String title;

    @Indexed
    private String leadId;

    @Indexed
    private String customerId;

    @Builder.Default
    private SalesStage salesStage = SalesStage.LEAD;

    private Double estimatedRevenue;

    private Double probabilityPercentage;

    private LocalDate expectedClosingDate;

    private String competitor;

    private String decisionMaker;

    private String nextAction;

    private String assignedEmployeeId;

    @Builder.Default
    private String status = "OPEN"; // OPEN, WON, LOST, ON_HOLD

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
