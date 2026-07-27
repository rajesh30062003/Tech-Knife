package com.techknife.backend.dto;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

public class RoleRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRoleRequest {
        @NotNull(message = "Role type is required")
        private Role role;

        @NotBlank(message = "Role name is required")
        private String name;

        private String description;

        private Set<Permission> permissions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRolePermissionsRequest {
        @NotNull(message = "Permissions set cannot be null")
        private Set<Permission> permissions;
    }
}
