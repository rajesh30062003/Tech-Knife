package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPendingStatusRequest {
    private String requestedStatus;
    private String reason;
    private String requestedBy;
    private String requestedByRole;
    private String requestedAt;
}
