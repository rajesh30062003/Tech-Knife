package com.techknife.leave.entity;

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

/**
 * MongoDB Document for configuring Leave Types (Casual, Sick, Earned, etc.).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_types")
public class LeaveType {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code; // e.g. "CL", "SL", "EL", "ML", "PL"

    private String name; // e.g. "Casual Leave"

    private String description;

    @Builder.Default
    private Double defaultAnnualQuota = 12.0;

    @Builder.Default
    private Boolean carryForwardAllowed = false;

    @Builder.Default
    private Double maxCarryForwardDays = 0.0;

    @Builder.Default
    private Boolean encashable = false;

    @Builder.Default
    private Boolean requiresAttachment = false;

    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
