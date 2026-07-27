package com.techknife.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "verification_otps")
public class VerificationOtp extends BaseEntity {

    public enum OtpType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }

    @Indexed
    private String email;

    private String otpCode;

    private OtpType type;

    private Instant expiryDate;

    private boolean used = false;
}
