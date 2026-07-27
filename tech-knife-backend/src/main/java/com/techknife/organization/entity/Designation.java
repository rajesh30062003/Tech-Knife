package com.techknife.organization.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB Document representing a Designation / Role Position within a Department/Company.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "designations")
public class Designation {

    @Id
    private String id;

    @Indexed
    private String companyId;

    @Indexed
    private String departmentId;

    @NotBlank(message = "Designation code is mandatory")
    @Indexed(unique = true)
    private String code;

    @NotBlank(message = "Designation name is mandatory")
    private String name;

    private String description;

    private Integer level;

    @Builder.Default
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
