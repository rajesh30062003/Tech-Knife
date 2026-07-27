package com.techknife.backend.dto;

import com.techknife.backend.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String designation;
    private String department;
    private String phoneNumber;
    private String avatarUrl;
    private Set<Role> roles;
    private Boolean enabled;
    private Boolean accountNonLocked;
}
