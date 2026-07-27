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
 * MongoDB document entity representing email verification tokens issued during user registration or email updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "User ID is required")
    private String userId;

    @Indexed(unique = true)
    @NotBlank(message = "Token string is required")
    private String token;

    @Indexed
    @NotNull(message = "Expiry timestamp is required")
    private Instant expiry;

    @Builder.Default
    private boolean verified = false;

    @CreatedDate
    private Instant createdAt;
}
