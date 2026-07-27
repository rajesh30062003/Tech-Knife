package com.techknife.iam.dto.response;

import com.techknife.iam.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response payload representing essential identity information for the currently authenticated principal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Currently authenticated user identity summary")
public class CurrentUserResponse {

    @Schema(description = "Unique user ID", example = "USR-2026-001")
    private String userId;

    @Schema(description = "Associated employee ID if applicable", example = "EMP-101")
    private String employeeId;

    @Schema(description = "Associated customer ID if applicable", example = "CUST-501")
    private String customerId;

    @Schema(description = "Official organizational email", example = "john.doe@techknife.com")
    private String officialEmail;

    @Schema(description = "Full user display name", example = "John Doe")
    private String name;

    @Schema(description = "Profile image asset URL", example = "https://res.cloudinary.com/techknife/profiles/usr001.jpg")
    private String profileImage;

    @Schema(description = "Granted role codes")
    private Set<String> roles;

    @Schema(description = "Granted permission codes")
    private Set<String> permissions;

    @Schema(description = "Current account state")
    private AccountStatus accountStatus;
}
