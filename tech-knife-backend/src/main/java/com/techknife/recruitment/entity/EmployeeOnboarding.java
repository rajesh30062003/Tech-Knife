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
@Document(collection = "employee_onboardings")
public class EmployeeOnboarding {

    @Id
    private String id;

    @Indexed
    private String candidateId;

    @Indexed
    private String applicationId;

    @Indexed
    private String offerLetterId;

    @Builder.Default
    private String documentCollectionStatus = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED

    @Builder.Default
    private List<String> collectedDocuments = new ArrayList<>();

    @Builder.Default
    private String verificationStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Builder.Default
    private String accountCreationStatus = "PENDING"; // PENDING, CREATED

    private String convertedEmployeeId;

    @Builder.Default
    private String onboardingStatus = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
