package com.techknife.backend.dto;

import com.techknife.backend.constant.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private String id;
    private Permission permission;
    private String code;
    private String name;
    private String category;
    private String description;
}
