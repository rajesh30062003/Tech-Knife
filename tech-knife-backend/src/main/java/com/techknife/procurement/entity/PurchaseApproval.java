package com.techknife.procurement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pro_purchase_approvals")
public class PurchaseApproval {

    @Id
    private String id;

    private String purchaseRequestId;

    private String requestNumber;

    private String approverId;

    private String approverName;

    private Integer approvalStep;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String comments;

    private LocalDate approvalDate;

    @CreatedDate
    private Instant createdAt;
}
