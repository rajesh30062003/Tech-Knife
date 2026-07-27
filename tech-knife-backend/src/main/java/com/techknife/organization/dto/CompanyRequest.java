package com.techknife.organization.dto;

import com.techknife.organization.entity.Company;
import com.techknife.organization.entity.OrganizationStatus;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company code is mandatory")
    private String code;

    @NotBlank(message = "Company name is mandatory")
    private String name;

    private String description;
    private String website;
    private String email;
    private String phone;
    private String taxId;
    private String registrationNumber;
    private Company.Address address;
    private OrganizationStatus status;
}
