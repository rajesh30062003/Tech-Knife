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
public class TeamRequest {

    private String companyId;
    private String branchId;
    private String departmentId;

    @NotBlank(message = "Team code is mandatory")
    private String code;

    @NotBlank(message = "Team name is mandatory")
    private String name;

    private String description;
    private String leaderId;
    private OrganizationStatus status;
}
