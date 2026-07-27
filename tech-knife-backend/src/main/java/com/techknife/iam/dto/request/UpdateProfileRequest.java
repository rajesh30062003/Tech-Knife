package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating user personal and profile metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for updating authenticated user profile attributes")
public class UpdateProfileRequest {

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Email(message = "Invalid personal email format")
    @Schema(description = "Secondary or personal email address", example = "john.personal@example.com")
    private String personalEmail;

    @Pattern(regexp = "^$|^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format (E.164 standard)")
    @Schema(description = "Primary contact mobile number", example = "+12025550143")
    private String mobile;

    @Schema(description = "Department or organizational unit", example = "Engineering")
    private String department;

    @Schema(description = "Designation or title", example = "Senior Software Engineer")
    private String designation;

    @Schema(description = "User bio or professional summary", example = "Full stack engineer specializing in cloud architectures")
    private String bio;
}
