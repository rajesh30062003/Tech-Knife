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
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String designation;
    private String department;
    private String phoneNumber;
    private String avatarUrl;
    private boolean enabled;
    private boolean accountNonLocked;
    private boolean emailVerified;
    private Set<Role> roles;
    private Set<Permission> permissions;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;
}
