package com.techknife.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

/**
 * Response payload representing system security roles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Security role details")
public class RoleResponse {

    @Schema(description = "Unique MongoDB document ID")
    private String id;

    @Schema(description = "Role identifier code", example = "ROLE_ADMIN")
    private String roleCode;

    @Schema(description = "Human readable role name", example = "System Administrator")
    private String roleName;

    @Schema(description = "Role description", example = "Full system administration access rights")
    private String description;

    @Schema(description = "Associated permission codes")
    private Set<String> permissions;

    @Schema(description = "Role active flag", example = "true")
    private boolean active;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
