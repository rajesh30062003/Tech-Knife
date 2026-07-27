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
public class BranchRequest {

    private String companyId;

    @NotBlank(message = "Branch code is mandatory")
    private String code;

    @NotBlank(message = "Branch name is mandatory")
    private String name;

    private String description;
    private Boolean isHeadquarters;
    private Company.Address address;
    private OrganizationStatus status;
}
