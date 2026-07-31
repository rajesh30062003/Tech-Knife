package com.techknife.backend.entity;

import com.techknife.backend.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Indexed
    private String userId;

    private String password;

    @Field("passwordHash")
    private String passwordHash;

    private String firstName;

    private String lastName;

    private String designation;

    private String department;

    private String phoneNumber;

    private String avatarUrl;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean emailVerified = false;

    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    private Set<String> permissions = new HashSet<>();

    private Instant lastLoginAt;

    public String getPassword() {
        if (password != null && !password.trim().isEmpty()) {
            return password;
        }
        return passwordHash;
    }
}
