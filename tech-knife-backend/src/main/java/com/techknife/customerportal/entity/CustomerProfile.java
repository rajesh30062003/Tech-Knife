package com.techknife.customerportal.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_profiles")
public class CustomerProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String customerAccountId;

    private String companyName;

    private String contactName;

    private String contactEmail;

    private String phone;

    private String secondaryPhone;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String taxId; // GST / VAT / PAN

    private String website;

    private String companySize;

    private String industry;

    private String avatarUrl;

    private String preferredLanguage;

    private String timezone;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
