package com.techknife.procurement.dto;

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
public class PurchaseApprovalDTO {

    private String id;

    @NotBlank(message = "Purchase Request ID is required")
    private String purchaseRequestId;

    private String requestNumber;

    private String approverId;

    private String approverName;

    private Integer approvalStep;

    private String status;

    private String comments;

    private LocalDate approvalDate;

    private Instant createdAt;
}
