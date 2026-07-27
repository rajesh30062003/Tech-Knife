package com.techknife.backend.entity;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "roles")
public class RoleEntity extends BaseEntity {

    @Indexed(unique = true)
    private Role role;

    private String name;

    private String description;

    private boolean isSystemRole = true;

    private Set<Permission> permissions = new HashSet<>();
}
