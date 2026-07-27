package com.techknife.timetracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetApprovalRequest {

    @NotBlank(message = "Approver ID is required")
    private String approverId;

    private String rejectionReason;
}
