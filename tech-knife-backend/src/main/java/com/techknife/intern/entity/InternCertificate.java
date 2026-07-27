package com.techknife.intern.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * MongoDB Document representing an issued Internship Completion Certificate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "intern_certificates")
public class InternCertificate {

    @Id
    private String id;

    @Indexed
    private String internId;

    @Indexed(unique = true)
    private String certificateNumber;

    @Indexed(unique = true)
    private String verificationCode;

    private LocalDate issueDate;

    private String downloadUrl;

    private String publicId; // Cloudinary public ID

    private String generatedBy;

    @CreatedDate
    private Instant createdAt;
}
