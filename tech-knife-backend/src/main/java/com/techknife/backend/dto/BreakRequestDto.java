package com.techknife.backend.dto;

import com.techknife.backend.constant.PunchType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakRequestDto {

    @NotNull(message = "Break punch type must be BREAK_START or BREAK_END")
    private PunchType punchType;

    private String location;
    private String ipAddress;
    private String notes;
}
