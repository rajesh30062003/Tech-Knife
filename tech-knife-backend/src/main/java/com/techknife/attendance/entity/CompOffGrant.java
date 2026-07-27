package com.techknife.attendance.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comp_off_grants")
public class CompOffGrant {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String employeeName;

    private LocalDate workedDate;

    @Builder.Default
    private Double daysGranted = 1.0;

    private String reason;

    @Indexed
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, EXPIRED, CONSUMED

    private LocalDate expiryDate;

    private String approverId;

    private String approverComments;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
