package com.techknife.recruitment.entity;

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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "job_postings")
public class JobPosting {

    @Id
    private String id;

    @Indexed(unique = true)
    private String jobCode;

    private String title;

    private String department;

    private String designation;

    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERN

    private String experience; // e.g. "2-5 years"

    private Double minSalary;

    private Double maxSalary;

    private String salaryRange;

    private String location;

    @Builder.Default
    private List<String> skillsRequired = new ArrayList<>();

    private String description;

    @Builder.Default
    private List<String> responsibilities = new ArrayList<>();

    @Builder.Default
    private List<String> qualifications = new ArrayList<>();

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, PUBLISHED, ON_HOLD, CLOSED

    private LocalDate applicationDeadline;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
