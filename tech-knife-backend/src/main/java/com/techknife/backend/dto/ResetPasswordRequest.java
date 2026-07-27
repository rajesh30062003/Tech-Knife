package com.techknife.backend.dto;

import com.techknife.backend.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Official email address is required")
    @Email(message = "Email address must be valid")
    private String email;

    @NotBlank(message = "Verification OTP is required")
    private String otpCode;

    @NotBlank(message = "New password is required")
    @ValidPassword
    private String newPassword;
}
