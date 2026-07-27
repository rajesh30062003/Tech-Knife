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

    /**
     * Factory method to build UserPrincipal from token claims without raw password.
     *
     * @param id          User ID
     * @param email       User Email
     * @param roles       Assigned roles
     * @param permissions Assigned permissions
     * @return UserPrincipal instance
     */
    public static UserPrincipal create(String id, String email, List<String> roles, Set<String> permissions) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if (roles != null) {
            roles.forEach(role -> {
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            });
        }

        if (permissions != null) {
            permissions.forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission)));
        }

        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .roles(roles != null ? roles : new ArrayList<>())
                .permissions(permissions != null ? permissions : new HashSet<>())
                .enabled(true)
                .accountNonLocked(true)
                .authorities(grantedAuthorities)
                .build();
    }

    /**
     * Factory method to build UserPrincipal with full user details including password.
     *
     * @param id               User ID
     * @param email            User Email
     * @param password         User Encrypted Password
     * @param enabled          Account Enabled flag
     * @param accountNonLocked Account Non Locked flag
     * @param roles            Assigned roles
     * @param permissions      Assigned permissions
     * @return UserPrincipal instance
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
