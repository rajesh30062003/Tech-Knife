package com.techknife.backend.dto;

import com.techknife.backend.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresInMs;
    private String userId;
    private String employeeId;
    private String customerId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String profilePhoto;
    private String role;
    private Set<Role> roles;
    private String designation;
    private String department;
    private List<String> permissions;
    private String organizationId;
    private String lastLogin;
}
