package com.techknife.backend.entity;

import com.techknife.backend.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseEntity {

    @Indexed(unique = true)
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String designation;

    private String department;

    private String phoneNumber;

    private String avatarUrl;

    private boolean enabled = true;

    private boolean accountNonLocked = true;

    private boolean emailVerified = false;

    private Set<Role> roles = new HashSet<>();

    private Instant lastLoginAt;
}
