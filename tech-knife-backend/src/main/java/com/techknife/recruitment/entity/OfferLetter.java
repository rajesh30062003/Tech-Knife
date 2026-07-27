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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "offer_letters")
public class OfferLetter {

    @Id
    private String id;

    @Indexed
    private String applicationId;

    @Indexed
    private String candidateId;

    @Indexed
    private String jobPostingId;

    private Double salary;

    private String designation;

    private LocalDate joiningDate;

    private LocalDate validityDate;

    @Builder.Default
    private String acceptanceStatus = "PENDING"; // PENDING, ACCEPTED, DECLINED, EXPIRED

    private String offerLetterUrl;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
