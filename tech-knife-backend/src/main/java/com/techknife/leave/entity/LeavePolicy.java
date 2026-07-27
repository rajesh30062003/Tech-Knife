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
 * MongoDB Document for Leave Policies customizable by department, designation, branch, gender, employee type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_policies")
public class LeavePolicy {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String name;

    @Indexed
    private String leaveTypeId;

    private String departmentId;

    private String designationId;

    private String branchId;

    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERN

    private String gender; // MALE, FEMALE, OTHER, ALL

    private Double annualQuota;

    private Integer maxConsecutiveDays;

    private Integer minNoticeDays;

    @Builder.Default
    private Boolean allowHalfDay = true;

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
