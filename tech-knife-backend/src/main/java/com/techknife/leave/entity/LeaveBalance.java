package com.techknife.leave.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB Document for tracking an employee's leave quota balance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_balances")
@CompoundIndexes({
    @CompoundIndex(name = "emp_type_year_idx", def = "{'employeeId': 1, 'leaveTypeId': 1, 'year': 1}", unique = true)
})
public class LeaveBalance {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    @Indexed
    private String leaveTypeId;

    private String leaveTypeName;

    private Integer year;

    @Builder.Default
    private Double allocatedDays = 0.0;

    @Builder.Default
    private Double accruedDays = 0.0;

    @Builder.Default
    private Double carryForwardDays = 0.0;

    @Builder.Default
    private Double usedDays = 0.0;

    @Builder.Default
    private Double pendingDays = 0.0;

    @Builder.Default
    private Double lapsedDays = 0.0;

    @Builder.Default
    private Double availableDays = 0.0;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Double getTotalAllocated() {
        return this.allocatedDays != null ? this.allocatedDays : 0.0;
    }

    public Double getUsed() {
        return this.usedDays != null ? this.usedDays : 0.0;
    }

    public Double getRemaining() {
        return this.availableDays != null ? this.availableDays : 0.0;
    }
}

