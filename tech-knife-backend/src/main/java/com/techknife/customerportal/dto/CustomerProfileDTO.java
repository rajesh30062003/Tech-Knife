package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileDTO {

    private String id;
    private String customerAccountId;
    private String customerCode;
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
    private String taxId;
    private String website;
    private String companySize;
    private String industry;
    private String avatarUrl;
    private String preferredLanguage;
    private String timezone;
    private Instant createdAt;
    private Instant updatedAt;
}
