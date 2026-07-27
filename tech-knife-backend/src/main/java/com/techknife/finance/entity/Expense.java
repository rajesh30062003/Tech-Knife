package com.techknife.finance.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_expenses")
public class Expense {

    @Id
    private String id;

    @Indexed(unique = true)
    private String expenseNumber;

    private String categoryId;

    private String categoryName;

    private String title;

    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    private LocalDate expenseDate;

    private String vendorId;

    private String vendorName;

    private String employeeId;

    private String salaryRunId; // Salary Expense Link

    private String costCenterId;

    private String financialYearId;

    private String receiptUrl;

    @Builder.Default
    private String approvalStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    private String approvedBy;

    private Instant approvedAt;

    private String description;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
