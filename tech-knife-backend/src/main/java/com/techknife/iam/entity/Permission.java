package com.techknife.iam.entity;

import com.techknife.iam.enums.PermissionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document entity defining fine-grained resource permission rights.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "permissions")
public class Permission {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Permission code is required")
    private String code;

    @NotBlank(message = "Resource name is required")
    private String resource;

    @NotNull(message = "Action type is required")
    private PermissionType action;

    private String description;

    private String category;

    @Builder.Default
    private boolean active = true;
}
