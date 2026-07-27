package com.techknife.intern.dto;

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
public class InternCertificateResponse {
    private String id;
    private String internId;
    private String certificateNumber;
    private String verificationCode;
    private LocalDate issueDate;
    private String downloadUrl;
    private String publicId;
    private String generatedBy;
    private Instant createdAt;
}
