package com.techknife.leave.dto;

import com.techknife.leave.entity.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApprovalDTO {

    @NotNull(message = "Approval status is required")
    private LeaveStatus status;

    private String comments;
}
