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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pro_suppliers")
public class Supplier {

    @Id
    private String id;

    @Indexed(unique = true)
    private String supplierCode;

    private String companyName;

    private String contactPerson;

    private String email;

    private String phone;

    private String gstNumber;

    private String panNumber;

    private String address;

    private Double rating;

    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Builder.Default
    private String status = "ACTIVE";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
