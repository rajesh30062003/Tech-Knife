package com.techknife.crm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "crm_leads")
public class Lead {

    @Id
    private String id;

    @Indexed(unique = true)
    private String leadNumber;

    private String companyName;

    private String contactPerson;

    @Indexed
    private String email;

    @Indexed
    private String phone;

    private String website;

    private String industry;

    private String companySize;

    private String country;

    private String state;

    private String city;

    @Builder.Default
    private LeadSource leadSource = LeadSource.WEBSITE;

    private String customSource;

    @Builder.Default
    private LeadPriority priority = LeadPriority.MEDIUM;

    @Builder.Default
    private LeadStatus leadStatus = LeadStatus.NEW;

    private String assignedEmployeeId;

    private String assignedEmployeeName;

    private Double expectedBudget;

    private LocalDate expectedStartDate;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
