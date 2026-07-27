package com.techknife.iam.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document entity representing multi-factor OTP verification challenges.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "otp_verifications")
public class OtpVerification {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "User ID or target recipient is required")
    private String userId;

    @NotBlank(message = "OTP code is required")
    private String otp;

    @Indexed
    @NotNull(message = "Expiry timestamp is required")
    private Instant expiry;

    @NotBlank(message = "OTP purpose is required")
    private String purpose;

    @Builder.Default
    private int attempts = 0;

    @Builder.Default
    private boolean verified = false;

    @CreatedDate
    private Instant createdAt;
}
