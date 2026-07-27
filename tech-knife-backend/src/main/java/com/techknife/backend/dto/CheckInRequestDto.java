package com.techknife.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequestDto {

    @NotBlank(message = "User ID is required for check-in")
    private String userId;

    private String userEmail;
    private String userName;
    private String department;

    private String location;
    private String ipAddress;
    private String notes;
    private boolean isWfh;
}
