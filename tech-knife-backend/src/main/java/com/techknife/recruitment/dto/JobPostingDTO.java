package com.techknife.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDTO {

    private String id;

    private String jobCode;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Department is required")
    private String department;

    private String designation;

    private String employmentType;

    private String experience;

    private Double minSalary;

    private Double maxSalary;

    private String salaryRange;

    private String location;

    private List<String> skillsRequired;

    private String description;

    private List<String> responsibilities;

    private List<String> qualifications;

    private String status;

    private LocalDate applicationDeadline;

    private Instant createdAt;

    private Instant updatedAt;
}
