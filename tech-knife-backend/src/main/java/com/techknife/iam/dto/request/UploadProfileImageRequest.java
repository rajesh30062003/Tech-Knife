package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating or linking a user profile picture avatar URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for setting user profile image avatar URL")
public class UploadProfileImageRequest {

    @NotBlank(message = "Profile image URL is required")
    @Schema(description = "URL of the uploaded profile image asset", example = "https://res.cloudinary.com/techknife/image/upload/v1/profiles/usr001.jpg")
    private String profileImageUrl;
}
