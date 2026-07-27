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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "job_applications")
public class Application {

    @Id
    private String id;

    @Indexed
    private String candidateId;

    @Indexed
    private String jobPostingId;

    @Builder.Default
    private String status = "APPLIED"; // APPLIED, SHORTLISTED, REJECTED, HOLD, INTERVIEW_SCHEDULED, OFFER_SENT, OFFER_ACCEPTED, OFFER_DECLINED, JOINED

    private Instant appliedDate;

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
