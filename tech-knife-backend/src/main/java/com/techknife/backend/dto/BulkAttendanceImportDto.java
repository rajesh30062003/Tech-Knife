package com.techknife.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceImportDto {

    @NotEmpty(message = "Attendance records list cannot be empty")
    private List<ManualAttendanceRequestDto> records;

    private String batchName;
    private String remarks;
}
