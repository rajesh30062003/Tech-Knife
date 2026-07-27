package com.techknife.payroll.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payroll_salary_components")
public class SalaryComponent {

    @Id
    private String id;

    @Indexed(unique = true)
    private String componentCode;

    private String componentName;

    private String componentType; // EARNING, DEDUCTION

    private String calculationType; // FIXED, PERCENTAGE

    private Double percentageValue;

    private String baseComponent;

    @Builder.Default
    private Boolean isTaxable = true;

    @Builder.Default
    private Boolean isStatutory = false;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
