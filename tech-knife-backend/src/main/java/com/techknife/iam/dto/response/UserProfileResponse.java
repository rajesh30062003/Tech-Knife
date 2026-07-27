package com.techknife.iam.dto.response;

import com.techknife.iam.enums.AccountStatus;
import com.techknife.iam.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

/**
 * Response payload representing detailed user profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed user profile response object")
public class UserProfileResponse {

    @Schema(description = "Unique user identifier", example = "USR-2026-001")
    private String userId;

    @Schema(description = "Official organizational email", example = "john.doe@techknife.com")
    private String officialEmail;

    @Schema(description = "Personal email address", example = "john.doe@gmail.com")
    private String personalEmail;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Full display name", example = "John Doe")
    private String fullName;

    @Schema(description = "Mobile contact number", example = "+12025550143")
    private String mobile;

    @Schema(description = "Profile picture asset URL", example = "https://res.cloudinary.com/techknife/profiles/usr001.jpg")
    private String profilePictureUrl;

    @Schema(description = "Department name", example = "Engineering")
    private String department;

    @Schema(description = "Job designation / title", example = "Lead Architect")
    private String designation;

    @Schema(description = "Associated employee ID if applicable", example = "EMP-001")
    private String employeeId;

    @Schema(description = "Associated customer ID if applicable", example = "CUST-101")
    private String customerId;

    @Schema(description = "Current account state", example = "ACTIVE")
    private AccountStatus accountStatus;

    @Schema(description = "Account classification type", example = "EMPLOYEE")
    private AccountType accountType;

    @Schema(description = "Email verification status", example = "true")
    private boolean emailVerified;

    @Schema(description = "Mobile verification status", example = "true")
    private boolean mobileVerified;

    @Schema(description = "Set of assigned role codes")
    private Set<String> roles;

    @Schema(description = "Set of granted permission codes")
    private Set<String> permissions;

    @Schema(description = "Account creation timestamp")
    private Instant createdAt;

    @Schema(description = "Profile last update timestamp")
    private Instant updatedAt;

    @Schema(description = "Last successful login timestamp")
    private Instant lastLogin;
}
