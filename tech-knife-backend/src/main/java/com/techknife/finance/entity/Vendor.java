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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fin_vendors")
public class Vendor {

    @Id
    private String id;

    @Indexed(unique = true)
    private String vendorCode;

    private String vendorName;

    private String email;

    private String phone;

    private String address;

    private String gstNumber;

    private String panNumber;

    // Bank details
    private String bankName;

    private String accountNumber;

    private String ifscCode;

    private String branchName;

    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
