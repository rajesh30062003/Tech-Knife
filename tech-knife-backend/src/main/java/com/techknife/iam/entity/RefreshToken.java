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
 * MongoDB document entity representing JWT refresh token credentials and device session state.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {

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
    private boolean revoked = false;

    private String device;

    private String deviceInfo;

    public String getDeviceInfo() {
        return this.deviceInfo != null ? this.deviceInfo : this.device;
    }


    private String ipAddress;

    @CreatedDate
    private Instant createdAt;
}
