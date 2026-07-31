package com.techknife.backend.mapper;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.AuthResponse;
import com.techknife.backend.dto.UserResponse;
import com.techknife.backend.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserMapper {

    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken, long expiresInMs) {
        if (user == null) return null;

        String primaryRole = "ROLE_EMPLOYEE";
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            primaryRole = user.getRoles().iterator().next().name();
        }

        Set<String> perms = new HashSet<>();
        if (user.getPermissions() != null) {
            perms.addAll(user.getPermissions());
        }

        boolean isExecutive = user.getRoles() != null && user.getRoles().stream().anyMatch(r ->
                r == Role.ROLE_SUPER_ADMIN || r == Role.ROLE_ADMIN || r == Role.ROLE_CEO ||
                r == Role.ROLE_MD || r == Role.ROLE_CTO || r == Role.ROLE_CMO || r == Role.ROLE_DIRECTOR
        );

        if (isExecutive) {
            perms.add("PROJECT_CREATE");
            perms.add("PROJECT_READ");
            perms.add("PROJECT_UPDATE");
            perms.add("PROJECT_DELETE");
            perms.add("PROJECT_ASSIGN");
            perms.add("PROJECT_STATUS_UPDATE");
            perms.add("PROJECT_LINK_UPDATE");
            perms.add("PROJECT_VIEW_ALL");
            for (Permission p : Permission.values()) {
                perms.add(p.name());
            }
        } else {
            perms.add("USER_READ");
            perms.add("PROJECT_READ");
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInMs(expiresInMs)
                .userId(user.getId())
                .employeeId(user.getId())
                .customerId(user.getId())
                .fullName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobile(user.getPhoneNumber() != null ? user.getPhoneNumber() : "")
                .profilePhoto(user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                .role(primaryRole)
                .roles(user.getRoles())
                .designation(user.getDesignation() != null ? user.getDesignation() : "Enterprise Specialist")
                .department(user.getDepartment() != null ? user.getDepartment() : "General")
                .permissions(new ArrayList<>(perms))
                .organizationId("TECH-KNIFE-ORG-001")
                .lastLogin(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : "")
                .build();
    }

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .designation(user.getDesignation())
                .department(user.getDepartment())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
