package com.techknife.iam.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document entity capturing user authentication event audit trail and device analytics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "login_histories")
public class LoginHistory {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String userEmail;

    @Indexed
    private Instant loginTime;

    private Instant logoutTime;

    private String ipAddress;

    private String browser;

    private String userAgent;


    private String operatingSystem;

    private String deviceType;

    private String location;

    private String status;

    private String deviceInfo;

    private String failureReason;

    public String getDeviceInfo() {
        return this.deviceInfo != null ? this.deviceInfo : this.browser;
    }

    public String getFailureReason() {
        return this.failureReason;
    }
}

