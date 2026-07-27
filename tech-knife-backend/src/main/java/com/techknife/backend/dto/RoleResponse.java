package com.techknife.backend.dto;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private String id;
    private Role role;
    private String name;
    private String description;
    private boolean isSystemRole;
    private Set<Permission> permissions;
    private Instant createdAt;
    private Instant updatedAt;
}
