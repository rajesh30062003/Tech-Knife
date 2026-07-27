package com.techknife.crm.dto;

import com.techknife.crm.entity.LeadPriority;
import com.techknife.crm.entity.LeadSource;
import com.techknife.crm.entity.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadDTO {
    private String id;
    private String leadNumber;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String website;
    private String industry;
    private String companySize;
    private String country;
    private String state;
    private String city;

    private LeadSource leadSource;
    private String customSource;
    private LeadPriority priority;
    private LeadStatus leadStatus;

    private String assignedEmployeeId;
    private String assignedEmployeeName;
    private Double expectedBudget;
    private LocalDate expectedStartDate;
    private String remarks;

    private Instant createdAt;
    private Instant updatedAt;
}
