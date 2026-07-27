package com.techknife.organization.dto;

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
public class DepartmentRequest {

    private String companyId;
    private String branchId;

    @NotBlank(message = "Department code is mandatory")
    private String code;

    @NotBlank(message = "Department name is mandatory")
    private String name;

    private String description;
    private String headId;
    private OrganizationStatus status;
}
