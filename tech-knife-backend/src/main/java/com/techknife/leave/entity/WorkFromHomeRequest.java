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
import java.time.LocalDate;

/**
 * MongoDB Document for Work From Home (WFH) requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wfh_requests")
public class WorkFromHomeRequest {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    private String departmentId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double totalDays;

    private String reason;

    private String workPlan;

    @Indexed
    @Builder.Default
    private WFHStatus status = WFHStatus.PENDING;

    private String approverId;

    private String approverName;

    private String approverComments;

    private Instant actionedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
