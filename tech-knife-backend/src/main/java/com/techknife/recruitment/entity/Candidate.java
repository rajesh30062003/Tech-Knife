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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "candidates")
public class Candidate {

    @Id
    private String id;

    @Indexed(unique = true)
    private String candidateCode;

    private String firstName;

    private String lastName;

    @Indexed
    private String email;

    private String phone;

    private String address;

    private String experience;

    private String currentCompany;

    private Double currentCtc;

    private Double expectedCtc;

    private String noticePeriod;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private String resumeUrl;

    private String portfolioUrl;

    private String linkedInUrl;

    private String gitHubUrl;

    @Builder.Default
    private String status = "NEW"; // NEW, SHORTLISTED, REJECTED, INTERVIEWING, OFFERED, HIRED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
