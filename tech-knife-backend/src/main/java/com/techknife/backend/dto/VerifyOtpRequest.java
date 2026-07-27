package com.techknife.backend.dto;

import com.techknife.backend.entity.VerificationOtp;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {

    @NotBlank(message = "Official email address is required")
    @Email(message = "Email address must be valid")
    private String email;

    @NotBlank(message = "OTP code is required")
    private String otpCode;

    @NotNull(message = "OTP type is required")
    private VerificationOtp.OtpType type;
}
