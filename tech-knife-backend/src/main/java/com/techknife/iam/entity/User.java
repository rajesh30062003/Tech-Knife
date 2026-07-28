package com.techknife.iam.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * MongoDB document entity capturing User profile, credentials, IAM authorization, and security state.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Indexed(unique = true)
    @NotBlank(message = "Official email is required")
    @Email(message = "Invalid official email format")
    private String officialEmail;

    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String mobile;

    private String profileImage;

    private String employeeId;

    private String customerId;

    private String department;

    private String designation;

    private String bio;

    public String getFullName() {
        if (firstName == null && lastName == null) return "";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }

    public void setFullName(String fullName) {
        if (fullName != null) {
            String[] parts = fullName.trim().split("\\s+", 2);
            this.firstName = parts[0];
            if (parts.length > 1) {
                this.lastName = parts[1];
            }
        }
    }

    public String getProfilePictureUrl() {
        return this.profileImage;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profileImage = profilePictureUrl;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getEmployeeId() {
        return this.employeeId != null ? this.employeeId : this.userId;
    }

    public String getCustomerId() {
        return this.customerId;
    }


    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

    @Builder.Default
    private AccountType accountType = AccountType.INTERNAL;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Builder.Default
    private Set<String> permissions = new HashSet<>();

    @Builder.Default
    private boolean emailVerified = false;

    @Builder.Default
    private boolean mobileVerified = false;

    private Instant lastLogin;

    private Instant lastPasswordChanged;

    @Builder.Default
    private int failedLoginAttempts = 0;

    @Builder.Default
    private boolean accountLocked = false;

    private Instant lockUntil;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    public Instant getLockExpiration() {
        return this.lockUntil;
    }

    public void setLockExpiration(Instant lockUntil) {
        this.lockUntil = lockUntil;
    }
}

