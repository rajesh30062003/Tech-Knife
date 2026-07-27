package com.techknife.iam.dto.response;

import com.techknife.iam.enums.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload representing granular authority permission details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Granular permission authority details")
public class PermissionResponse {

    @Schema(description = "Unique MongoDB document ID")
    private String id;

    @Schema(description = "Permission identifier code", example = "EMPLOYEE_READ")
    private String permissionCode;

    @Schema(description = "Human readable permission name", example = "Read Employee Records")
    private String permissionName;

    @Schema(description = "Target resource entity", example = "EMPLOYEE")
    private String resource;

    @Schema(description = "Action type allowed on resource", example = "READ")
    private String action;

    @Schema(description = "Functional category group", example = "HR_MANAGEMENT")
    private String category;

    @Schema(description = "Permission classification type", example = "SYSTEM")
    private PermissionType type;

    @Schema(description = "Detailed permission description", example = "Allows viewing employee profiles and directory details")
    private String description;

    @Schema(description = "Permission active flag", example = "true")
    private boolean active;
}
