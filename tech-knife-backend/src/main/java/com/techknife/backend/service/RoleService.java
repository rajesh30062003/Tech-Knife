package com.techknife.backend.service;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.RoleRequest;
import com.techknife.backend.dto.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    RoleResponse getRoleByEnum(Role role);
    RoleResponse getRoleById(String id);
    RoleResponse createRole(RoleRequest.CreateRoleRequest request);
    RoleResponse updateRolePermissions(String id, Set<Permission> permissions);
    void deleteRole(String id);
}
