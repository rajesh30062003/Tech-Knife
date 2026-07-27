package com.techknife.organization.dto;

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
public class DesignationResponse {
    private String id;
    private String companyId;
    private String departmentId;
    private String code;
    private String name;
    private String description;
    private Integer level;
    private OrganizationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
