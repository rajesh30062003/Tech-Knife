package com.techknife.procurement.entity;

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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pro_purchase_requests")
public class PurchaseRequest {

    @Id
    private String id;

    @Indexed(unique = true)
    private String requestNumber;

    private String requestedById;

    private String requestedByName;

    private String departmentId;

    private String departmentName;

    private LocalDate requestDate;

    private LocalDate requiredDate;

    @Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, ORDERED, COMPLETED, CANCELLED

    @Builder.Default
    private List<PurchaseRequestItem> items = new ArrayList<>();

    @Builder.Default
    private BigDecimal totalEstimatedAmount = BigDecimal.ZERO;

    private String justification;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
