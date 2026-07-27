package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_customers")
public class Customer {

    @Id
    private String id;

    @Indexed(unique = true)
    private String customerCode;

    private String companyName;

    @Indexed
    private String gstNumber;

    @Indexed
    private String pan;

    private String industry;

    private String businessType;

    private String website;

    private CustomerContact primaryContact;

    @Builder.Default
    private List<CustomerContact> contacts = new ArrayList<>();

    private CustomerAddress billingAddress;

    private CustomerAddress shippingAddress;

    private String accountManagerId;

    private String accountManagerName;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
