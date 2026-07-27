package com.techknife.organization.dto;

import com.techknife.organization.entity.Company;
import com.techknife.organization.entity.OrganizationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private String website;
    private String email;
    private String phone;
    private String taxId;
    private String registrationNumber;
    private Company.Address address;
    private OrganizationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
