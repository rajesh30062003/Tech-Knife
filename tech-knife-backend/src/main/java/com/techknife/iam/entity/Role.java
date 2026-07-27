package com.techknife.iam.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * MongoDB document entity defining system roles and mapped permission sets.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
public class Role {

    /**
     * Default System Role Constants
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_MD = "MD";
    public static final String ROLE_CEO = "CEO";
    public static final String ROLE_CTO = "CTO";
    public static final String ROLE_COO = "COO";
    public static final String ROLE_HR_MANAGER = "HR_MANAGER";
    public static final String ROLE_FINANCE_MANAGER = "FINANCE_MANAGER";
    public static final String ROLE_PROJECT_MANAGER = "PROJECT_MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_INTERN = "INTERN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Role code is required")
    private String roleCode;

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String description;

    @Builder.Default
    private Set<String> permissions = new HashSet<>();

    @Builder.Default
    private boolean systemRole = false;

    @Builder.Default
    private boolean active = true;
}
