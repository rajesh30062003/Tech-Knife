package com.techknife.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Principal user details model representing the authenticated user in Spring Security Context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private String id;
    private String email;

    @JsonIgnore
    private String password;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    private Set<String> permissions = new HashSet<>();

    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(com.techknife.backend.entity.User user) {
        List<String> roles = user.getRoles() != null
                ? user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
                : new ArrayList<>();

        Set<String> permissions = user.getPermissions() != null
                ? new HashSet<>(user.getPermissions())
                : new HashSet<>();

        return create(user.getId(), user.getEmail(), user.getPassword(), user.isEnabled(), user.isAccountNonLocked(), roles, permissions);
    }

    /**
     * Factory method to build UserPrincipal from token claims without raw password.
     */
    public static UserPrincipal create(String id, String email, List<String> roles, Set<String> permissions) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        List<String> cleanRoles = roles != null ? roles : new ArrayList<>();
        Set<String> cleanPermissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();

        // 1. Add Role Authorities (both ROLE_XXX and XXX for robust Spring Security matching)
        for (String role : cleanRoles) {
            String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            grantedAuthorities.add(new SimpleGrantedAuthority(roleName));
            grantedAuthorities.add(new SimpleGrantedAuthority(roleName.replace("ROLE_", "")));
        }

        // 2. Auto-confer Executive Project & Employee Permissions for Executive/HR Roles
        boolean isExecutive = cleanRoles.stream().anyMatch(r ->
                r.equalsIgnoreCase("ROLE_SUPER_ADMIN") || r.equalsIgnoreCase("SUPER_ADMIN") ||
                r.equalsIgnoreCase("ROLE_ADMIN") || r.equalsIgnoreCase("ADMIN") ||
                r.equalsIgnoreCase("ROLE_CEO") || r.equalsIgnoreCase("CEO") ||
                r.equalsIgnoreCase("ROLE_MD") || r.equalsIgnoreCase("MD") ||
                r.equalsIgnoreCase("ROLE_CTO") || r.equalsIgnoreCase("CTO") ||
                r.equalsIgnoreCase("ROLE_COO") || r.equalsIgnoreCase("COO") ||
                r.equalsIgnoreCase("ROLE_CMO") || r.equalsIgnoreCase("CMO") ||
                r.equalsIgnoreCase("ROLE_HR") || r.equalsIgnoreCase("HR") ||
                r.equalsIgnoreCase("ROLE_HR_MANAGER") || r.equalsIgnoreCase("HR_MANAGER")
        );

        if (isExecutive) {
            cleanPermissions.add("PROJECT_CREATE");
            cleanPermissions.add("PROJECT_READ");
            cleanPermissions.add("PROJECT_UPDATE");
            cleanPermissions.add("PROJECT_DELETE");
            cleanPermissions.add("PROJECT_ASSIGN");
            cleanPermissions.add("PROJECT_STATUS_UPDATE");
            cleanPermissions.add("PROJECT_LINK_UPDATE");
            cleanPermissions.add("PROJECT_VIEW_ALL");

            cleanPermissions.add("EMPLOYEE_CREATE");
            cleanPermissions.add("EMPLOYEE_READ");
            cleanPermissions.add("EMPLOYEE_UPDATE");
            cleanPermissions.add("EMPLOYEE_DELETE");
            cleanPermissions.add("EMPLOYEE_VIEW");
        }

        // Auto-confer Internal Messaging Authority for All Active Organization Roles
        boolean isInternalUser = cleanRoles.stream().anyMatch(r ->
                r.equalsIgnoreCase("ROLE_SUPER_ADMIN") || r.equalsIgnoreCase("SUPER_ADMIN") ||
                r.equalsIgnoreCase("ROLE_ADMIN") || r.equalsIgnoreCase("ADMIN") ||
                r.equalsIgnoreCase("ROLE_CEO") || r.equalsIgnoreCase("CEO") ||
                r.equalsIgnoreCase("ROLE_MD") || r.equalsIgnoreCase("MD") ||
                r.equalsIgnoreCase("ROLE_CTO") || r.equalsIgnoreCase("CTO") ||
                r.equalsIgnoreCase("ROLE_COO") || r.equalsIgnoreCase("COO") ||
                r.equalsIgnoreCase("ROLE_CMO") || r.equalsIgnoreCase("CMO") ||
                r.equalsIgnoreCase("ROLE_HR") || r.equalsIgnoreCase("HR") ||
                r.equalsIgnoreCase("ROLE_HR_MANAGER") || r.equalsIgnoreCase("HR_MANAGER") ||
                r.equalsIgnoreCase("ROLE_PROJECT_MANAGER") || r.equalsIgnoreCase("PROJECT_MANAGER") ||
                r.equalsIgnoreCase("ROLE_EMPLOYEE") || r.equalsIgnoreCase("EMPLOYEE")
        );

        if (isInternalUser) {
            cleanPermissions.add("MESSAGE_SEND");
        }

        // 3. Add Permission Authorities
        for (String perm : cleanPermissions) {
            grantedAuthorities.add(new SimpleGrantedAuthority(perm));
        }

        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .roles(cleanRoles)
                .permissions(cleanPermissions)
                .enabled(true)
                .accountNonLocked(true)
                .authorities(grantedAuthorities)
                .build();
    }

    /**
     * Factory method to build UserPrincipal with full user details including password.
     */
    public static UserPrincipal create(String id, String email, String password, boolean enabled, boolean accountNonLocked, List<String> roles, Set<String> permissions) {
        UserPrincipal principal = create(id, email, roles, permissions);
        principal.setPassword(password);
        principal.setEnabled(enabled);
        principal.setAccountNonLocked(accountNonLocked);
        return principal;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
