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
public class DesignationRequest {

    private String companyId;
    private String departmentId;

    @NotBlank(message = "Designation code is mandatory")
    private String code;

    @NotBlank(message = "Designation name is mandatory")
    private String name;

    private String description;
    private Integer level;
    private OrganizationStatus status;
}
